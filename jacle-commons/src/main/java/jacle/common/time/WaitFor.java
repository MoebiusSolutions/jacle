package jacle.common.time;
import jacle.common.lang.RuntimeInterruptedException;
import jacle.common.thread.SleepProvider;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Provides a means of waiting for a condition to be true. See {@link #run()}.
 * 
 * @author rkenney
 */
public class WaitFor implements Runnable {

	public static final long DEFAULT_SLEEP_COUNT = 100;
	public static final TimeUnit DEFAULT_SLEEP_UNIT = TimeUnit.MILLISECONDS;
	
	private TimeProvider timeProvider = TimeProvider.DEFAULT;
	private SleepProvider sleepProvider = SleepProvider.DEFAULT;
	private final Condition condition;
	private long sleepCount = DEFAULT_SLEEP_COUNT;
	private TimeUnit sleepUnits = DEFAULT_SLEEP_UNIT;
	private long timeoutCount;
	private TimeUnit timeoutUnits;

	public static interface Condition {
		boolean isTrue();
	}
	
	public WaitFor(Condition condition) {
		this.condition = condition;
	}
	
	public static class TimeoutException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public TimeoutException(String message) {
			super(message);
		}
	}
	
	/**
	 * Defines the duration of sleep between attempts of evaluating the
	 * {@link Condition}. Defaults to {@link #DEFAULT_SLEEP_COUNT} and
	 * {@link #DEFAULT_SLEEP_UNIT}. Setting the count to 0 skips the sleep.
	 */
	public WaitFor withSleep(long sleepCount, TimeUnit sleepUnits) {
		this.sleepCount = sleepCount;
		this.sleepUnits = sleepUnits;
		return this;
	}

	/**
	 * Defines the maximum time to retry the {@link Condition} before failing
	 * with a {@link TimeoutException}. Defaults to no timeout.
	 */
	public WaitFor withTimeout(long timeoutCount, TimeUnit timeoutUnits) {
		this.timeoutCount = timeoutCount;
		this.timeoutUnits = timeoutUnits;
		return this;
	}

	// For unit testing
	WaitFor withTimeProvider(TimeProvider provider) {
		this.timeProvider = provider;
		return this;
	}
	
	// For unit testing
	WaitFor withSleepProvider(SleepProvider provider) {
		this.sleepProvider = provider;
		return this;
	}
	
	/**
	 * Executes {@link Condition} repeatedly until it returns <code>true</code>.
	 * Sleeps for the duration specified by {@link #withSleep(long, TimeUnit)}
	 * between executions of {@link Condition}. Guaranteed to execute the
	 * {@link Condition} at least once, even if the
	 * {@link #withTimeout(long, TimeUnit)} is set to zero.</p>
	 * 
	 * @throws TimeoutException
	 *             The {@link Condition} is not evaluated to <code>true</code>
	 *             within the time specified by
	 *             {@link #withTimeout(long, TimeUnit)}
	 * 
	 * @throws RuntimeInterruptedException
	 *             An {@link InterruptedException} was thrown while the thread
	 *             was sleeping
	 */
	public void run() throws TimeoutException, RuntimeInterruptedException {
		Date timeout = null;
		if (timeoutCount > 0) {
			timeout = new Date(timeProvider.getTime().getTime()+timeoutUnits.toMillis(timeoutCount));
		}
		while (!condition.isTrue()) {
			if (timeout != null && !timeout.after(timeProvider.getTime())) {
				throw new TimeoutException(String.format("Condition failed to pass within the timeout of [%s %s]", timeoutCount, timeoutUnits));
			}
			if (sleepCount > 0) {
				try {
					sleepProvider.sleep(this.sleepUnits.toMillis(this.sleepCount));
				} catch (InterruptedException e) {
					throw new RuntimeInterruptedException(e);
				}
			}
		}
	}
}
