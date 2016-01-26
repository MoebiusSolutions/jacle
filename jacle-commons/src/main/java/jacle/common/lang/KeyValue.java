package jacle.common.lang;

/**
 * A generic key/value object
 * 
 * @param <K>
 *            The key type
 * @param <V>
 *            The value type
 * 
 * @author rkenney
 */
public class KeyValue<K,V> {

	private K key;
	private V value;

	public KeyValue(K key, V value) {
		this.setKey(key);
		this.setValue(value);
	}

	public KeyValue() {}

	public K getKey() {
		return key;
	}

	/**
	 * Sets the key
	 * 
	 * @param key
	 *            The key
	 * 
	 * @return "this" (fluent setter)
	 */
	public KeyValue<K, V> setKey(K key) {
		this.key = key;
		return this;
	}

	public V getValue() {
		return value;
	}

	/**
	 * Sets the value
	 * 
	 * @param value
	 *            The value
	 * 
	 * @return "this" (fluent setter)
	 */
	public KeyValue<K, V> setValue(V value) {
		this.value = value;
		return this;
	}
}
