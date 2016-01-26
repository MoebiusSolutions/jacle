package jacle.commontest.junit;

import java.util.Comparator;

/**
 * Similar to {@link Comparator}, but performs a simple
 * {@link Object#equals(Object)}
 * 
 * @param <T>
 *            The type to compare
 * 
 * @author rkenney
 */
public interface Equator<T> {

	/**
	 * Returns true iff a and b are equal in whatever terms the implementation
	 * is intended for
	 * 
	 * @param a
	 *            First object to compare
	 * @param b
	 *            Second object to compare
	 * 
	 * @return The result
	 */
	public boolean equals(T a, T b);
}
