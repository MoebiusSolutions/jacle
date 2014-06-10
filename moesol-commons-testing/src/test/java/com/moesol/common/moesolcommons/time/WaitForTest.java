package com.moesol.common.moesolcommons.time;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

import com.moesol.common.moesolcommons.lang.Ref;
import com.moesol.common.moesolcommons.thread.SleepProvider;
import com.moesol.common.moesolcommons.time.WaitFor.TimeoutException;

public class WaitForTest {

	@Test
	public void testTimeout() throws Exception {
		final Ref<Integer> conditionCheckCount = new Ref<Integer>(0);
		WaitFor waitFor = new WaitFor(new WaitFor.Condition() {public boolean isTrue() {
			conditionCheckCount.set(conditionCheckCount.get()+1);
			return false;
		}});
		waitFor.withSleep(1, TimeUnit.SECONDS);
		waitFor.withTimeout(5, TimeUnit.SECONDS);

		final MockTimeProvider mockTime = new MockTimeProvider();
		final IncrementingMockSleepProvider mockSleep = new IncrementingMockSleepProvider(mockTime);
		waitFor.withTimeProvider(mockTime).withSleepProvider(mockSleep);
		
		// Run and verify exception
		try {
			waitFor.run();
			Assert.fail("Expected exception");
		} catch (TimeoutException e) {
			// Success
		}
		
		// Verify expected number of sleep() calls
		assertEquals(5, mockSleep.sleepCount);

		// Verify expected number of condition checks
		assertEquals((Integer)6, conditionCheckCount.get());
	}
	
	@Test
	public void testImmediateSuccess() throws Exception {
		final Ref<Integer> conditionCheckCount = new Ref<Integer>(0);
		WaitFor waitFor = new WaitFor(new WaitFor.Condition() {public boolean isTrue() {
			conditionCheckCount.set(conditionCheckCount.get()+1);
			return true;
		}});
		waitFor.withSleep(1, TimeUnit.SECONDS);
		waitFor.withTimeout(5, TimeUnit.SECONDS);

		final MockTimeProvider mockTime = new MockTimeProvider();
		final IncrementingMockSleepProvider mockSleep = new IncrementingMockSleepProvider(mockTime);
		waitFor.withTimeProvider(mockTime).withSleepProvider(mockSleep);
		
		// Run and verify no exception
		waitFor.run();
		
		// Verify expected number of sleep() calls
		assertEquals(0, mockSleep.sleepCount);

		// Verify expected number of condition checks
		assertEquals((Integer)1, conditionCheckCount.get());
	}

	@Test
	public void testSuccessAfterOneFalse() throws Exception {
		final Ref<Integer> conditionCheckCount = new Ref<Integer>(0);
		final LinkedList<Boolean> conditionResults = new LinkedList<Boolean>(Arrays.asList(new Boolean[]{false, true}));
		WaitFor waitFor = new WaitFor(new WaitFor.Condition() {public boolean isTrue() {
			conditionCheckCount.set(conditionCheckCount.get()+1);
			return conditionResults.removeFirst();
		}});
		waitFor.withSleep(1, TimeUnit.SECONDS);
		waitFor.withTimeout(5, TimeUnit.SECONDS);

		final MockTimeProvider mockTime = new MockTimeProvider();
		final IncrementingMockSleepProvider mockSleep = new IncrementingMockSleepProvider(mockTime);
		waitFor.withTimeProvider(mockTime).withSleepProvider(mockSleep);
		
		// Run and verify no exception
		waitFor.run();
		
		// Verify expected number of sleep() calls
		assertEquals(1, mockSleep.sleepCount);

		// Verify expected number of condition checks
		assertEquals((Integer)2, conditionCheckCount.get());
	}
	
	private static class IncrementingMockSleepProvider implements SleepProvider {

		private MockTimeProvider mockTime;
		private int sleepCount;

		public IncrementingMockSleepProvider(MockTimeProvider mockTime) {
			this.mockTime = mockTime;
		}
		
		@Override
		public void sleep(long millis) throws InterruptedException {
			mockTime.addMillis(millis);
			sleepCount++;
		}
	}
}
