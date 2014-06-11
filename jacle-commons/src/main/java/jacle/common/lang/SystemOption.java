package jacle.common.lang;

public class SystemOption {

	/**
	 * Static accessor
	 */
	public static SystemOption I = new SystemOption();
	
    /**
	 * Searches for the value of a configurable value by searching the system
	 * properties (e.g. -D options) and then the environment variables. If
	 * neither of these are found, the default value is returned.
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
}
