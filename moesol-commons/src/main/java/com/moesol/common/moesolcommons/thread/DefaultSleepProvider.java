package com.moesol.common.moesolcommons.thread;


public class DefaultSleepProvider implements SleepProvider {

	@Override
	public void sleep(long millis) throws InterruptedException {
		Thread.sleep(millis);
	}
}
