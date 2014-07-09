package jacle.commontest.junit;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Test;

public class AssertExtTest {

	/**
	 * Verifies {@link AssertExt#assertEquals(Object, Object, Equator)}
	 */
	@Test
	public void testAssertEquals() {
		
		/*
		 * Define test cases
		 */
		List<TestCase<String>> testCases = new ArrayList<>();
		// Both null is equal (regardless of equator)
		testCases.add(new TestCase<String>().setExpected(null).setActual(null).setEquator(new NotCalledEquator<String>()).setExpectFailure(false));
		// One null is not equal (regardless of equator)
		testCases.add(new TestCase<String>().setExpected("expected").setActual(null).setEquator(new NotCalledEquator<String>()).setExpectFailure(true));
		testCases.add(new TestCase<String>().setExpected(null).setActual("actual").setEquator(new NotCalledEquator<String>()).setExpectFailure(true));
		// Equator decides if both not null
		testCases.add(new TestCase<String>().setExpected("expected").setActual("actual").setEquator(new MockEquator<String>(false)).setExpectFailure(true));
		testCases.add(new TestCase<String>().setExpected("expected").setActual("actual").setEquator(new MockEquator<String>(true)).setExpectFailure(false));

		/*
		 * Execute test cases
		 */
		for (TestCase<String> testCase : testCases) {
			boolean assertionFailed = false;
			try {
				AssertExt.assertEquals(testCase.expected, testCase.actual, testCase.equator);
			} catch (AssertionFailedError e) {
				assertionFailed = true;
			}
			if (testCase.expectFailure && !assertionFailed) {
				Assert.fail("Expected exception");
			}
			if (!testCase.expectFailure && assertionFailed) {
				Assert.fail("Unexpected exception");
			}
		}
	}

	public void setUp() throws Exception {
		NotCalledEquator.reset();
	}
	
	public void tearDown() throws Exception {
		NotCalledEquator.verify();
	}
	
	private static class TestCase<T> {
		private T expected;
		private T actual;
		private Equator<T> equator;
		private boolean expectFailure;
		public TestCase<T> setExpected(T expected) {
			this.expected = expected;
			return this;
		}
		public TestCase<T> setActual(T actual) {
			this.actual = actual;
			return this;
		}
		public TestCase<T> setEquator(Equator<T> equator) {
			this.equator = equator;
			return this;
		}
		public TestCase<T> setExpectFailure(boolean expectFailure) {
			this.expectFailure = expectFailure;
			return this;
		}
	}
	
	/**
	 * Asserts that <code>equator.equals(expected, actual)</code>
	 */
	public static <T> void assertComparesEqual(String failMessage, T expected, T actual, Equator<T> comparator) {
		if (expected == actual) {
			return;
		}
		if (expected == null || actual == null) {
			Assert.fail(failMessage);
		}
		if (!comparator.equals(expected, actual)) {
			Assert.fail(failMessage);
		}
	}

	private static class MockEquator<T> implements Equator<T> {
		
		private boolean result;

		public MockEquator(boolean result) {
			this.result = result;
		}
		
		@Override
		public boolean equals(T a, T b) {
			return result;
		}
	}

	/**
	 * An {@link Equator} that should never be executed. This should be verified
	 * with {@link NotCalledEquator#verify()} a teardown so that the failure
	 * Exception isn't swallowed by other testing.
	 * 
	 * @author rkenney
	 */
	private static class NotCalledEquator<T> implements Equator<T> {
		
		private static boolean wasCalled;
		
		@Override
		public boolean equals(T a, T b) {
			wasCalled = true;
			throw new RuntimeException("This should not have been called");
		}

		public static void verify() {
			if (wasCalled) {
				Assert.fail("Should not have been called.");
			}
		}

		public static void reset() {
			wasCalled = false;
		}
	}
}
