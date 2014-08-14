package jacle.common.lang;

/**
 * A generic key/value object
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
	 * Non-void return provided for fluent style programming (e.g. returns this)
	 */
	public KeyValue<K, V> setKey(K key) {
		this.key = key;
		return this;
	}

	public V getValue() {
		return value;
	}

	/**
	 * Non-void return provided for fluent style programming (e.g. returns this)
	 */
	public KeyValue<K, V> setValue(V value) {
		this.value = value;
		return this;
	}
}
