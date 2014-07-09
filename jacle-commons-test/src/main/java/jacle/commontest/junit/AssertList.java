package jacle.commontest.junit;

import java.util.List;

import junit.framework.Assert;

public class AssertList {

	/**
	 * Verifies that both lists are not null, contain the same number of
	 * entries, and that each entry is either null in both lists or true under
	 * the condition <code>expected.equals(actual)</code>.
	 */
	public static <T> void assertEquals(List<T> expected, List<T> actual) {
		assertEquals(expected, actual,
				new Equator<T>() { public boolean equals(T expectedO, T actualO) {
					return expectedO.equals(actualO);
				}});
	}

	/**
	 * Verifies that both lists are not null, contain the same number of
	 * entries, and that each entry is either null in both lists or true under
	 * the condition <code>equator.equals(expected, actual)</code>.
	 */
	public static <T> void assertEquals(List<T> expected, List<T> actual, Equator<T> equator) {
		assertEquals(null, expected, actual, equator);
	}

	/**
	 * Verifies that both lists are not null, contain the same number of
	 * entries, and that each entry is either null in both lists or true under
	 * the condition <code>equator.equals(expected, actual)</code>.
	 */
	public static <T> void assertEquals(String failMessage, List<T> expected, List<T> actual, Equator<T> equator) {
		if (expected == null) {
			Assert.fail("Expected list is null"); 
		}
		if (actual == null) {
			Assert.fail("Actual list is null"); 
		}
		Assert.assertEquals(expected.size(), actual.size());
		for (int i=0; i<expected.size(); i++) {
			T entryA = expected.get(i);
			T entryB = actual.get(i);
			String msg = failMessage;
			if (failMessage == null) {
				msg = String.format("Entry [%s] is not equal, with values [%s] and [%s]", i, entryA, entryB);
			}
			AssertExt.assertEquals(msg, entryA, entryB, equator);
		}
	}
}
