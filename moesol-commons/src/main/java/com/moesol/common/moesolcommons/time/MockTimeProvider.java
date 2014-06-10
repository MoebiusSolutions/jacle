package com.moesol.common.moesolcommons.time;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link TimeProvider} that is useful for unit testing
 * 
 * @author rkenney
 */
public class MockTimeProvider implements TimeProvider {
	
	Date currentTime;

	@Override
	public Date getTime() {
		return this.currentTime;
	}
	
	/**
	 * Creates a mock time instance initialized to the current system time
	 */
	public MockTimeProvider() {
		this.currentTime = new Date();
	}
	
	/**
	 * Creates a mock time instance initialized to the provided time
	 */
	public MockTimeProvider(Date date) {
		this.currentTime = date;
	}
	
	/**
	 * Adds to the mock clock time
	 */
	public Date add(long incrementCount, TimeUnit incrementUnits) {
		this.currentTime = new Date(this.currentTime.getTime() + incrementUnits.toMillis(incrementCount));
		return this.currentTime;
	}

	/**
	 * Adds to the mock clock time
	 */
	public Date addMillis(long millis) {
		return add(millis, TimeUnit.MILLISECONDS);
	}

	/**
	 * Adds to the mock clock time
	 */
	public Date addSeconds(long seconds) {
		return add(seconds, TimeUnit.SECONDS);
	}

	/**
	 * Adds to the mock clock time
	 */
	public Date addMinutes(long minutes) {
		return add(minutes, TimeUnit.MINUTES);
	}

	/**
	 * Adds to the mock clock time
	 */
	public Date addHours(long hours) {
		return add(hours, TimeUnit.HOURS);
	}

	/**
	 * Adds days to the mock clock time, according to a calendar. Note that this
	 * means that the increment may not be the standard number of seconds (due
	 * to leap seconds).
	 */
	public Date addDaysByCalendar(int days) {
		return addByCalendar(days, Calendar.DATE);
	}
	
	/**
	 * Adds days to the mock clock time, according to a calendar. Note that this
	 * means that the increment may not be the standard number of days or seconds (due
	 * to leap days and seconds).
	 */
	public Date addWeeksByCalendar(int weeks) {
		return addByCalendar(weeks, Calendar.WEEK_OF_YEAR);
	}
	
	/**
	 * Adds months to the mock clock time, according to a calendar. Note that this
	 * means that the increment may not be the standard number of days or seconds (due
	 * to leap days and seconds).
	 */
	public Date addMonthsByCalendar(int months) {
		return addByCalendar(months, Calendar.MONTH);
	}
	
	/**
	 * Adds years to the mock clock time, according to a calendar. Note that this
	 * means that the increment may not be the standard number of days or seconds (due
	 * to leap days and seconds).
	 */
	public Date addYearsByCalendar(int days) {
		return addByCalendar(days, Calendar.YEAR);
	}
	
	private Date addByCalendar(int count, int calendarUnit) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.currentTime);
		calendar.add(calendarUnit, count);
		this.currentTime = calendar.getTime();
		return this.currentTime;
	}
}
