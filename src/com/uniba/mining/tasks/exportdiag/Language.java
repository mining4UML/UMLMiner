package com.uniba.mining.tasks.exportdiag;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Configuration class for handling language settings.
 * pasqualeardimento
 */
public class Language {
	private static ResourceBundle messages;
	private static Locale currentLocale;



	private Language() {
		// Set the default language to English
		currentLocale = new Locale("en");

		// Load the resource bundle for the current language
		loadResourceBundle();
	}

	/**
	 * Gets the ResourceBundle for language settings.
	 *
	 * @return The ResourceBundle for language settings.
	 */
	public static ResourceBundle getMessages() {
		if (messages == null)
			new Language();
		return messages;
	}

	/**
	 * Set the language based on the provided language code.
	 *
	 * @param languageCode The language code (e.g., "en" for English, "it" for Italian).
	 */
	public static void setLanguage(String languageCode) {
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