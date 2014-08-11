package jacle.common.lang;

/**
 * A generic handler class
 * 
 * @author rkenney
 */
public interface Handler<T> {
	public void handle(T item);
}

