package com.moesol.common.moesolcommons.thread;


public interface SleepProvider {

	public static final SleepProvider DEFAULT = new DefaultSleepProvider();
	
	public void sleep(long millis) throws InterruptedException;
}
