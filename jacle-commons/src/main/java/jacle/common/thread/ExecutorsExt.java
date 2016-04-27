package jacle.common.thread;

import java.util.concurrent.ExecutorService;
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
     * Identical to {@link Executors#newScheduledThreadPool(int)} with the
     * addition of prepending the provided string to the thread names.
     */
    public ScheduledExecutorService newScheduledThreadPool(int numThreads, String threadNamePrefix) {
        return Executors.newScheduledThreadPool(numThreads, new NamedThreadFactory(threadNamePrefix));
    }
    
    /**
     * Identical to {@link Executors#newFixedThreadPool(int)} with the addition of
     * prepending the provided string to the thread names.
     */
    public ExecutorService newFixedThreadPool(int numThreads, String threadNamePrefix) {
        return Executors.newFixedThreadPool(numThreads, new NamedThreadFactory(threadNamePrefix));
    }
    
    /**
     * Identical to {@link Executors#newCachedThreadPool()} with the addition of
     * prepending the provided string to the thread names.
     */
    public ExecutorService newCachedThreadPool(String threadNamePrefix) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(threadNamePrefix));
    }
}
