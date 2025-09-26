package translation;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Main Objs
            Translator translator = new JSONTranslator(); // fetch country-langs and convert(translate)
            CountryCodeConverter countryConverter = new CountryCodeConverter(); // used for country code to full name
            LanguageCodeConverter languageConverter = new LanguageCodeConverter(); // used for lang code to full name

            /// countries scrollable ///
            // Fetch full countries
            List<String> countries = translator.getCountryCodes();

            // Change code to full name
            String[] countryNames = countries.stream()
                    .map(countryConverter::fromCountryCode)
                    .toArray(String[]::new);

            // Make JList with full names
            JList<String> countryList = new JList<>(countryNames);

            // List settings
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane countryScroll = new JScrollPane(countryList);
            countryScroll.setPreferredSize(new Dimension(420, 200));

            /// Flags ///
            // Load flag icons for each country code
            Map<String, ImageIcon> flags = loadFlagIcons(countryNames, countryConverter);

            countryList.setCellRenderer((list, value, index, isSelected,
                                         cellHasFocus) -> {
                JLabel label = new JLabel(value);
                label.setIcon(flags.get(value));
                label.setHorizontalTextPosition(SwingConstants.RIGHT);
                label.setIconTextGap(10);
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                    label.setOpaque(true);
                } else {
                    label.setOpaque(false);
                }
                return label;
            });

            /// language dropdown ///
            // Fetch full languages
            List<String> languages = translator.getLanguageCodes();

            //change code to full
            String[] langNames = languages.stream()
                    .map(languageConverter::fromLanguageCode)
                    .toArray(String[]::new);

            // Make JList with full names
            JComboBox<String> languageCombo = new JComboBox<>(langNames);

            /// result
            // result label placeholder
            JLabel resultLabel = new JLabel("Translation will appear here");
            resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
            resultLabel.setForeground(new Color(50, 50, 50));
            resultLabel.setBorder(new EmptyBorder(10, 0, 10, 0)); // tp/bp
            resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // horizontally centered

            // country section
            JPanel countryPanel = new JPanel(new BorderLayout(5, 5));
            countryPanel.add(new JLabel("Select Country:"), BorderLayout.NORTH);
            countryPanel.add(countryScroll, BorderLayout.CENTER);
            countryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // language section
            JPanel languagePanel = new JPanel(new BorderLayout(5, 5));
            languagePanel.add(new JLabel("Select Language:"), BorderLayout.NORTH);
            languagePanel.add(languageCombo, BorderLayout.CENTER);
            languagePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            /// panel setup ///
            //main
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // attach to panel
            mainPanel.add(languagePanel);
            mainPanel.add(resultLabel);
            mainPanel.add(countryPanel);

            // Event listeners
            ListSelectionListener updateTranslation = e -> {
                String selectedCountry = countryList.getSelectedValue();
                String selectedLanguage = (String) languageCombo.getSelectedItem();
                if (selectedCountry != null && selectedLanguage != null) {
                    String translation = translator.translate(countryConverter.fromCountry(selectedCountry),
                            languageConverter.fromLanguage(selectedLanguage));
                    if (translation == null) translation = "No translation found!";
                    resultLabel.setText(translation);
                }
            };

            countryList.addListSelectionListener(updateTranslation);
            languageCombo.addActionListener(e -> updateTranslation.valueChanged(null));

            // Frame setup
            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    /**
     * Helper to preload flags
     **/
    private static Map<String, ImageIcon> loadFlagIcons(String[] countryNames, CountryCodeConverter converter) {
        Map<String, ImageIcon> map = new HashMap<>();
        for (String name : countryNames) {
            String code = converter.fromCountryTwo(name);
            URL url = GUI.class.getResource("/flags/" + code.toLowerCase() + ".png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(24, 16, Image.SCALE_SMOOTH);
                map.put(name, new ImageIcon(img));
            } else {
                map.put(name, null);
            }
        }
        return map;
    }

}
