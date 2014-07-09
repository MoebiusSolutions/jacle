package jacle.commontest.junit;

import junit.framework.Assert;

/**
 * Adds methods that are logical extensions of {@link Assert}
 * 
 * @author rkenney
 */
public class AssertExt {

	/**
	 * Assert equality using an {@link Equator}
	 */
	public static <T> void assertEquals(T expected, T actual, Equator<T> equator) {
		assertEquals(String.format("Not equal, with values [%s] and [%s]", expected, actual),
				expected, actual, equator);
	}

	/**
	 * Assert equality using an {@link Equator}
	 */
	public static <T> void assertEquals(String failMessage, T expected, T actual, Equator<T> equator) {
		if (expected == actual) {
			return;
		}
		if (expected == null || actual == null) {
			Assert.fail(failMessage);
		}
		if (!equator.equals(expected, actual)) {
			Assert.fail(failMessage);
		}
	}
}
