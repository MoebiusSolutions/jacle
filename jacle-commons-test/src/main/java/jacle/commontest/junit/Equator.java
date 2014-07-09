package jacle.commontest.junit;

import java.util.Comparator;

/**
 * Similar to {@link Comparator}, but performs a simple {@link #equals(Object)}
 * 
 * @author rkenney
 */
public interface Equator<T> {

	/**
	 * Returns true iff a and b are equal in whatever terms the implementation
	 * is intended for
	 */
	public boolean equals(T a, T b);
}
