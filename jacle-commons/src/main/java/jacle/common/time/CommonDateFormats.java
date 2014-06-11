package jacle.common.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonDateFormats {

    public static final String FORMAT_TO_DAYS = "yyyy-MM-dd"; 
    public static final String FORMAT_TO_SECS = "yyyy-MM-dd HH:mm:ss"; 
    public static final String FORMAT_TO_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS"; 

    public static final String FORMAT_TO_DAYS_FOR_FILE = "yyyy-MM-dd"; 
    public static final String FORMAT_TO_SECS_FOR_FILE = "yyyy-MM-dd_HH-mm-ss"; 
    public static final String FORMAT_TO_MILLIS_FOR_FILE = "yyyy-MM-dd_HH-mm-ss-SSS"; 

    /**
     * Returns a date format to the resolution of days:</p>
     * <code>
     * yyyy-MM-dd
     * </code>
     */
    public static SimpleDateFormat getFormatToDays() {
    	return new SimpleDateFormat(FORMAT_TO_DAYS);
    }

    /**
     * Returns a date format to the resolution of seconds:</p>
     * <code>
     * yyyy-MM-dd HH:mm:ss
     * </code>
     */
    public static SimpleDateFormat getFormatToSecs() {
    	return new SimpleDateFormat(FORMAT_TO_SECS);
    }

    /**
     * Returns a date format to the resolution of milliseconds:</p>
     * <code>
     * yyyy-MM-dd HH:mm:ss.SSS
     * </code>
     */
    public static SimpleDateFormat getFormatToMillis() {
    	return new SimpleDateFormat(FORMAT_TO_MILLIS);
    }

    /**
     * Formats a {@link Date} to the resolution of days:</p>
     * <code>
     * yyyy-MM-dd
     * </code>
     */
  	public static String formatToDays(Date date) {
		return getFormatToDays().format(date);
	}

    /**
     * Formats a {@link Date} to the resolution of seconds:</p>
     * <code>
     * yyyy-MM-dd HH:mm:ss
     * </code>
     */
	public static String formatToSecs(Date date) {
		return getFormatToSecs().format(date);
	}

    /**
     * Formats a {@link Date} to the resolution of milliseconds:</p>
     * <code>
     * yyyy-MM-dd HH:mm:ss.SSS
     * </code>
     */
	public static String formatToMillis(Date date) {
		return getFormatToMillis().format(date);
	}

    /**
     * Returns a date format to the resolution of days, for use in filenames:</p>
     * <code>
     * yyyy-MM-dd
     * </code>
     */
    public static SimpleDateFormat getFormatToDaysForFilename() {
    	return new SimpleDateFormat(FORMAT_TO_DAYS_FOR_FILE);
    }

    /**
     * Returns a date format to the resolution of seconds, for use in filenames:</p>
     * <code>
     * yyyy-MM-dd_HH-mm-ss
     * </code>
     */
    public static SimpleDateFormat getFormatToSecsForFilename() {
    	return new SimpleDateFormat(FORMAT_TO_SECS_FOR_FILE);
    }

    /**
     * Returns a date format to the resolution of milliseconds, for use in filenames:</p>
     * <code>
     * yyyy-MM-dd_HH-mm-ss-SSS
     * </code>
     */
    public static SimpleDateFormat getFormatToMillisForFilename() {
    	return new SimpleDateFormat(FORMAT_TO_MILLIS_FOR_FILE);
    }

    /**
     * Formats a {@link Date} to the resolution of days, for use in filenames:</p>
     * <code>
     * yyyy-MM-dd
     * </code>
     */
  	public static String formatToDaysForFilename(Date date) {
		return getFormatToDaysForFilename().format(date);
	}

    /**
     * Formats a {@link Date} to the resolution of seconds, for use in filenames:</p>
     * <code>
     * yyyy-MM-dd_HH-mm-ss
     * </code>
     */
	public static String formatToSecsForFilename(Date date) {
		return getFormatToSecsForFilename().format(date);
	}

    /**
     * Formats a {@link Date} to the resolution of milliseconds, for use in filenames:</p>
     * <code>
     * yyyy-MM-dd_HH-mm-ss-SSS
     * </code>
     */
	public static String formatToMillisForFilename(Date date) {
		return getFormatToMillisForFilename().format(date);
	}
}
