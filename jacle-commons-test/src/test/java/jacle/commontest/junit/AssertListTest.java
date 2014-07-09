package jacle.commontest.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Test;

public class AssertListTest {

	public void setUp() throws Exception {
		NotCalledEquator.reset();
	}
	
	public void tearDown() throws Exception {
		NotCalledEquator.verify();
	}

	/**
	 * Verifies {@link AssertList#assertEquals(List, List)}
	 */
	@Test
	public void testAssertEquals() {
		
		/*
		 * Define test cases
		 */
		List<TestCase<String>> testCases = new ArrayList<>();
		// Both null is a failure
		testCases.add(new TestCase<String>().
				setExpected(null).
				setActual(null).
				setExpectFailure(true));
		// Either is null is a failure
		testCases.add(new TestCase<String>().
				setExpected(null).
				setActual(Arrays.asList(new String[]{"value"})).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value"})).
				setActual(null).
				setExpectFailure(true));
		// Both empty lists is a success
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{})).
				setActual(Arrays.asList(new String[]{})).
				setExpectFailure(false));
		// Null entry and empty list is a failure
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{null})).
				setActual(Arrays.asList(new String[]{})).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{})).
				setActual(Arrays.asList(new String[]{null})).
				setExpectFailure(true));
		// String entry and empty list is a failure
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value"})).
				setActual(Arrays.asList(new String[]{})).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{})).
				setActual(Arrays.asList(new String[]{"value"})).
				setExpectFailure(true));
		// Differing number of non-null entries is a failure
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value", "value"})).
				setActual(Arrays.asList(new String[]{"value"})).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value"})).
				setActual(Arrays.asList(new String[]{"value", "value"})).
				setExpectFailure(true));
		// Comparison results differing on the last item results is a failure
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value", "value", "value"})).
				setActual(Arrays.asList(new String[]{"value", "value", "bad-value"})).
				setExpectFailure(true));
		// Comparison results not differing is a success
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value", "value", "value"})).
				setActual(Arrays.asList(new String[]{"value", "value", "value"})).
				setExpectFailure(false));

		/*
		 * Execute test cases
		 */
		int i=0;
		for (TestCase<String> testCase : testCases) {
			executeTestCase(testCase, ""+(i++));
		}
	}

	/**
	 * Verifies {@link AssertList#assertEquals(List, List, Equator)}
	 */
	@Test
	public void testAssertEquals_WithEquator() {
		
		/*
		 * Define test cases
		 */
		List<TestCase<String>> testCases = new ArrayList<>();
		// Both null is a failure (regardless of equator)
		testCases.add(new TestCase<String>().
				setExpected(null).
				setActual(null).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		// Either is null is a failure (regardless of equator)
		testCases.add(new TestCase<String>().
				setExpected(null).
				setActual(Arrays.asList(new String[]{"actual"})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"expected"})).
				setActual(null).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		// Both empty lists is a success (regardless of equator)
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{})).
				setActual(Arrays.asList(new String[]{})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(false));
		// Null entry and empty list is a failure (regardless of equator)
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{null})).
				setActual(Arrays.asList(new String[]{})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{})).
				setActual(Arrays.asList(new String[]{null})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		// String entry and empty list is a failure (regardless of equator)
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value"})).
				setActual(Arrays.asList(new String[]{})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{})).
				setActual(Arrays.asList(new String[]{"value"})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		// Differing number of non-null entries is a failure (regardless of equator)
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value", "value"})).
				setActual(Arrays.asList(new String[]{"value"})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		testCases.add(new TestCase<String>().
				setExpected(Arrays.asList(new String[]{"value"})).
				setActual(Arrays.asList(new String[]{"value", "value"})).
				setEquator(new NotCalledEquator<String>()).
				setExpectFailure(true));
		// Comparison results differing on the last item results is a failure
		testCases.add(new TestCase<String>().
				// Note: these values are ignored in comparison
				setExpected(Arrays.asList(new String[]{"expected", "expected", "expected"})).
				setActual(Arrays.asList(new String[]{"actual", "actual", "actual"})).
				setEquator(new MockEquator<String>(true, true, false)).
				setExpectFailure(true));
		// Comparison results not differing is a success
		testCases.add(new TestCase<String>().
				// Note: these values are ignored in comparison
				setExpected(Arrays.asList(new String[]{"expected", "expected", "expected"})).
				setActual(Arrays.asList(new String[]{"actual", "actual", "actual"})).
				setEquator(new MockEquator<String>(true, true, true)).
				setExpectFailure(false));

		/*
		 * Execute test cases
		 */
		int i=0;
		for (TestCase<String> testCase : testCases) {
			testCase.execute(""+(i++));
		}
	}

	private <T> void executeTestCase(TestCase<T> testCase, String testCaseName) {
	}
	
	private static class TestCase<T> {
		private List<T> expected;
		private List<T> actual;
		private Equator<T> equator;
		private boolean expectFailure;
		public TestCase<T> setExpected(List<T> expected) {
			this.expected = expected;
			return this;
		}
		public TestCase<T> setActual(List<T> actual) {
			this.actual = actual;
			return this;
		}
		/**
		 * Set IFF if you wish to have the {@link Equator} compatible method to
		 * be tested.
		 */
		public TestCase<T> setEquator(Equator<T> equator) {
			this.equator = equator;
			return this;
		}
		public TestCase<T> setExpectFailure(boolean expectFailure) {
			this.expectFailure = expectFailure;
			return this;
		}
		
		public void execute(String testCaseName) {
			boolean assertionFailed = false;
			try {
				if (this.equator == null) {
					AssertList.assertEquals(this.expected, this.actual);
				} else {
					AssertList.assertEquals(this.expected, this.actual, this.equator);
				}
			} catch (AssertionFailedError e) {
				assertionFailed = true;
			}
			if (this.expectFailure && !assertionFailed) {
				Assert.fail("Expected exception for test case "+testCaseName);
			}
			if (!this.expectFailure && assertionFailed) {
				Assert.fail("Unexpected exception for test case "+testCaseName);
			}
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
		
		private LinkedList<Boolean> results = new LinkedList<Boolean>();

		public MockEquator(Boolean... results) {
			this.results.addAll(Arrays.asList(results));
		}
		
		@Override
		public boolean equals(T a, T b) {
			return results.pop();
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
