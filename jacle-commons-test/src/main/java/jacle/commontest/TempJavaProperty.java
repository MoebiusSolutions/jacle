package jacle.commontest;

import java.util.Properties;



/**
 * This class sets a java property and caches the old value so it can be reset.
 * It also handles null values cleanly.
 * 
 * @author rkenney
 */
public class TempJavaProperty {

	private String key;
	private String value;
	private String originalValue;
	private boolean wasSet;

	/**
	 * The key/value to set (temporarily) on the system. If the value is null,
	 * {@link Properties#remove(Object)} is called to remove the property.</p>
	 * 
	 * The system properties are not affected until {@link #set()} is
	 * called.</p>
	 */
	public TempJavaProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Applies the key/value, caching the old value first. Call {@link #reset()}
	 * to revert to the old value.
	 * 
	 * @return This object. For fluent coding.
	 */
	public TempJavaProperty set() {
		originalValue = System.getProperty(key);
		setJavaProperty(key, value);
		wasSet = true;
		return this;
	}
	
	/**
	 * Reverts the system property value to what was set at the time of calling
	 * {@link #set()}. Has no effect if {@link #set()} was never called or
	 * {@link #reset()} was already called.
	 */
	public void reset() {
		if (!wasSet) {
			return;
		}
		setJavaProperty(key, originalValue);
		wasSet = false;
	}
	
	private static void setJavaProperty(String propertyName, String value) {
		if (value == null) {
			System.getProperties().remove(propertyName);
		} else {
			System.setProperty(propertyName, value);
		}
	}
}
