package jacle.common.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Adds features to the core class {@link Executors}.
 * 
 * @author rkenney
 */
public class ExecutorsExt {

	/**
	 * Static accessor
	 */
	public static ExecutorsExt I = new ExecutorsExt();
	
	/**
	 * Returns a {@link ScheduledExecutorService} in which all threads have a
	 * prefix of the provided string.
	 * 
	 * @param numThreads
	 *            Number of threads to pool
	 * @param threadNamePrefix
	 *            Name to prefix the threads with
	 * 
	 * @return The result
	 */
	public ScheduledExecutorService newScheduledThreadPool(int numThreads, String threadNamePrefix) {
		return Executors.newScheduledThreadPool(numThreads, new NamedThreadFactory(threadNamePrefix));
	}
}
