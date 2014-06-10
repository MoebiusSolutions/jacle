package com.moesol.common.moesolcommons.lang;


/**
 * A simple indirect reference object that's commonly used to deal with the
 * situation where a final variable is needed for the result of work done in an
 * anonymous inner class.
 * 
 * @author rkenney
 */
public class Ref<T> {

	private T value;

	public Ref() {
		this(null);
	}

	public Ref(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
}
