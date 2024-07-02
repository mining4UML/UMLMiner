package com.uniba.mining.tasks.exportdiag;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Configuration class for handling language settings. pasqualeardimento
 */
public class Language {
    private static ResourceBundle messages;
    private static Locale currentLocale;
    private static Language instance;

    // Private constructor to prevent instantiation
    private Language() {
        // Set the default language to English
        currentLocale = new Locale("it");
        // Load the resource bundle for the current language
        loadResourceBundle();
    }

    private Language(String language) {
        // Set the language based on the provided language code
        switch (language) {
            case "en":
                currentLocale = new Locale("en");
                break;
            case "it":
                currentLocale = new Locale("it");
                break;
            default:
                currentLocale = new Locale("en");
        }
        // Load the resource bundle for the current language
        loadResourceBundle();
    }

    // Method to get the singleton instance
    public static Language getInstance() {
        if (instance == null) {
            instance = new Language();
        }
        return instance;
    }

    // Method to get the singleton instance with a specific language
    public static Language getInstance(String language) {
        if (instance == null) {
            instance = new Language(language);
        } else {
             instance.setLanguage(language);
        }
        return instance;
    }

    /**
     * Gets the ResourceBundle for language settings.
     *
     * @return The ResourceBundle for language settings.
     */
    public ResourceBundle getMessages() {
        return messages;
    }

    /**
     * Set the language based on the provided language code.
     *
     * @param languageCode The language code (e.g., "en" for English, "it" for Italian).
     */
    public void setLanguage(String languageCode) {
        currentLocale = new Locale(languageCode);
        loadResourceBundle();
    }

    /**
     * Load the resource bundle for the current language.
     */
    private static void loadResourceBundle() {
        messages = ResourceBundle.getBundle("messages", currentLocale);
    }
}
