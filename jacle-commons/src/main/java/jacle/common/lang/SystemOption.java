package jacle.common.lang;

import java.io.File;

public class SystemOption {

	/**
	 * Static accessor
	 */
	public static SystemOption I = new SystemOption();

	/**
	 * Searches for the value of a configurable value by searching the system
	 * properties (e.g. -D options) and then the environment variables. If
	 * neither of these are found, the default value is returned.
	 * 
	 * @param key
	 *            The key to search for
	 * @param defaultValue
	 *            The default value
	 * 
	 * @return The result
	 */
	public String getString(String key, String defaultValue) {
		String returnValue = System.getProperty(key);
		if (null != returnValue) {
			return returnValue;
		}
		returnValue = System.getenv(key);
		if (null != returnValue) {
			return returnValue;
		}
		return defaultValue;
	}

	/**
	 * Searches for the value of a configurable value by searching the system
	 * properties (e.g. -D options) and then the environment variables. If
	 * neither of these are found, the default value is returned.
	 * 
	 * @param key
	 *            The key to search for
	 * @param defaultValue
	 *            The default value
	 * 
	 * @return The matched string as a {@link File}.
	 */
	public File getFile(String key, File defaultValue) {
		String value = getString(key, null);
		if (value != null) {
			return new File(value);
		}
		return defaultValue;
	}
}
