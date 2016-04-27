package jacle.common.logging;

import jacle.common.lang.JavaUtil;
import jacle.common.lang.StringsExt;
import jacle.common.time.TimeProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * A {@link Formatter} for the Java logging subsystem that prefixes every line
 * break with a brief, configurable header. When formatting a message, this
 * presumes that an implicit newline should be injected at the end of each
 * message. See the unit tests to see how this works.
 * 
 * @author rkenney
 */
public class PrefixLogFormatter extends Formatter {
	
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

	private boolean showDate = true;
	private boolean showLogLevel = true;
	private boolean showLoggerName = true;
	private String appName = null;
	private DateFormat m_dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
	
	private static TimeProvider mockTimeProvider;
	
	public PrefixLogFormatter withShowDate(boolean showDate) {
		this.showDate = showDate;
		return this;
	}
	
	public PrefixLogFormatter withDateFormat(DateFormat format) {
		if (format == null) {
			this.m_dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		} else {
			this.m_dateFormat = format;
		}
		return this;
	}

	public PrefixLogFormatter withShowLogLevel(boolean showLogLevel) {
		this.showLogLevel = showLogLevel;
		return this;
	}

	public PrefixLogFormatter withAppName(String appName) {
		this.appName = appName;
		return this;
	}

	public PrefixLogFormatter withShowLoggerName(boolean doShow) {
		this.showLoggerName = doShow;
		return this;
	}

	// Package protected (for unit testing)
	PrefixLogFormatter withTimeProvider(TimeProvider mockTime) {
		mockTimeProvider = mockTime;
		return this;
	}
	
	// Override point to add extra information to the header
	protected String getExtraHeaderContent(LogRecord record) {
		return null;
	}
	
	@Override
	public String format(LogRecord record) {
		StringBuilder header = new StringBuilder();
		// Create header
		String nextPrefix = "[";
		if (appName != null) {
			header.append(nextPrefix).append(appName);
			nextPrefix = " ";
		}
		if (showDate) {
			header.append(nextPrefix).append(getDateString(record.getMillis()));
			nextPrefix = " ";
		}
		if (showLogLevel) {
			String level = Strings.padEnd(record.getLevel().toString(), 8, ' ');
			header.append(nextPrefix).append(level);
			nextPrefix = " ";
		}
		if (showLoggerName) {
			String simplifiedName = "";
			// Simplify the logger name, but only if it's not anonymous
			if (!StringsExt.I.isNullOrBlank(record.getLoggerName())) {
				simplifiedName = JavaUtil.I.getSimpleClassName(record.getLoggerName());
			}
			String trimmedName = Strings.padEnd(StringsExt.substring(simplifiedName, 0, 20), 20, ' ');
			header.append(nextPrefix).append(trimmedName);
			nextPrefix = " ";
		}
		
		// Extra data should be the last entry
		String extraContent = getExtraHeaderContent(record);
		if (extraContent != null && !extraContent.isEmpty()) {
			header.append(nextPrefix).append(extraContent);
			nextPrefix = " ";
		}
		
		// If some portion of the header was written, terminate the header
		if (!"[".equals(nextPrefix)) {
			header.append("] ");
		}
		// Create message with any exception
		String message = formatMessage(record);
		if (record.getThrown() != null) {
			message = String.format("%s%n%s%n", message, Throwables.getStackTraceAsString(record.getThrown()));
		}
		// Prefix all lines with header
		message = StringsExt.I.prefixLines(message, header.toString());
		// Ensure final newline
		if (!endsWithNewline(message)) {
			message = message + System.lineSeparator();
		}
		return message;
	}

	private boolean endsWithNewline(String message) {
		if (message.length() < 1) {
			return false;
		}
		// This covers Windows and Unix newlines
		return message.charAt(message.length()-1) == '\n';
	}

	private String getDateString(long millis) {
		Date time;
		if (mockTimeProvider != null) {
			time = mockTimeProvider.getTime();
		} else {
			time = new Date(millis);
		}
		return m_dateFormat.format(time);
	}
}
