package at.fhhagenberg.esd.sqe.ws20.gui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message class used for the externalization of the strings. 
 * Provides functionality to search for strings with keys which are specified in the messages.properties file.
 */
public class Messages {
	private static final String BUNDLE_NAME = "at.fhhagenberg.esd.sqe.ws20.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Simple CTOR for the given Messages class.
	 */
	private Messages() {
	}

	/**
	 * Returns a string from the resource bundle. The argument must specify a string in the 
	 * given resource bundle.
	 * If no string can be found a modified string of the key is returned.
	 * 
	 * @param key String key to determine the output string
	 * @return the string associated with the input key
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
