package jacle.common.logging;

import jacle.common.lang.JavaUtil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class PrefixLogFormatterTest {

	private static final Pattern UNIVERSAL_NEWLINE = Pattern.compile("\r?\n");
	private static final String DATE_PATTERN = "[0-9]{4}+\\-[0-9]{2}+\\-[0-9]{2}+ [0-9]{2}+:[0-9]{2}+:[0-9]{2}+ [\\+\\-][0-9]{4}+";
	private static final String GENERIC_HEADER_PATTERN = "\\[[^\\]]+\\]";

	@Test
	public void testDefaultOptions() throws Exception {
		/*
		 * Create logger with specific app name
		 */
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrefixLogFormatter formatter = new PrefixLogFormatter();
		Logger logger = createLogger(output, formatter, JavaUtil.I.getMethodName());
		/*
		 * Log some messages
		 */
		logger.info("My info message");
		logger.warning("My warning message");
		logger.log(Level.SEVERE, "My severe message with exception", new Exception("My exception message"));
		/*
		 * Verify the output
		 */
		logger.getHandlers()[0].flush();
		String outputString = new String(output.toByteArray());
		String[] outputLines = UNIVERSAL_NEWLINE.split(outputString, -1);
		assertMatchesPattern("\\["+DATE_PATTERN+" INFO     "+JavaUtil.I.getMethodName()+"  \\] My info message", outputLines[0]);
		assertMatchesPattern("\\["+DATE_PATTERN+" WARNING  "+JavaUtil.I.getMethodName()+"  \\] My warning message", outputLines[1]);
		assertMatchesPattern("\\["+DATE_PATTERN+" SEVERE   "+JavaUtil.I.getMethodName()+"  \\] My severe message with exception", outputLines[2]);
	}

	@Test
	public void testAppName() throws Exception {
		/*
		 * Create logger with specific app name
		 */
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrefixLogFormatter formatter = new PrefixLogFormatter();
		// Set app name
		formatter.withAppName("MyApp");
		Logger logger = createLogger(output, formatter, JavaUtil.I.getMethodName());
		/*
		 * Log some messages
		 */
		logger.info("My info message");
		logger.warning("My warning message");
		logger.log(Level.SEVERE, "My severe message with exception", new Exception("My exception message"));
		/*
		 * Verify the output contains the app name
		 */
		logger.getHandlers()[0].flush();
		String outputString = new String(output.toByteArray());
		String[] outputLines = UNIVERSAL_NEWLINE.split(outputString, -1);
		assertMatchesPattern("\\[MyApp "+DATE_PATTERN+" INFO     "+JavaUtil.I.getMethodName()+"         \\] My info message", outputLines[0]);
		assertMatchesPattern("\\[MyApp "+DATE_PATTERN+" WARNING  "+JavaUtil.I.getMethodName()+"         \\] My warning message", outputLines[1]);
		assertMatchesPattern("\\[MyApp "+DATE_PATTERN+" SEVERE   "+JavaUtil.I.getMethodName()+"         \\] My severe message with exception", outputLines[2]);
	}
	
	@Test
	public void testLoggerName() throws Exception {
		/*
		 * Create logger with specific app name
		 */
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrefixLogFormatter formatter = new PrefixLogFormatter();
		Logger longNameLogger = createLogger(output, formatter, "a.dummy.class.with.long.package.AndAVeryLongClassName");
		Logger shortNameLogger = createLogger(output, formatter, "a.Class");
		Logger longNonClassNameLogger= createLogger(output, formatter, "1234567890123456789012345678901234567890");
		Logger shortNonClassNameLogger= createLogger(output, formatter, "12345");
		
		/*
		 * Log some messages
		 */
		longNameLogger.info("longNameLogger");
		longNameLogger.getHandlers()[0].flush();
		shortNameLogger.info("shortNameLogger");
		shortNameLogger.getHandlers()[0].flush();
		longNonClassNameLogger.info("longNonClassNameLogger");
		longNonClassNameLogger.getHandlers()[0].flush();
		shortNonClassNameLogger.info("shortNonClassNameLogger");
		shortNonClassNameLogger.getHandlers()[0].flush();
		/*
		 * Verify the output
		 */
		String outputString = new String(output.toByteArray());
		String[] outputLines = UNIVERSAL_NEWLINE.split(outputString, -1);
		// Long names are simplified and then trimmed
		assertMatchesPattern("\\["+DATE_PATTERN+" INFO     AndAVeryLongClassNam\\] longNameLogger", outputLines[0]);
		// Short names are simplified and then padded
		assertMatchesPattern("\\["+DATE_PATTERN+" INFO     Class               \\] shortNameLogger", outputLines[1]);
		// Non-class names are only trimmed/padded
		assertMatchesPattern("\\["+DATE_PATTERN+" INFO     12345678901234567890\\] longNonClassNameLogger", outputLines[2]);
		assertMatchesPattern("\\["+DATE_PATTERN+" INFO     12345               \\] shortNonClassNameLogger", outputLines[3]);
	}
	
	/**
	 * Verifies that all newlines are preserved in the output, and that there's
	 * an implied newline followin every message
	 */
	@Test
	public void testLinebreakFormatting() throws Exception {
		/*
		 * Create logger with specific app name
		 */
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrefixLogFormatter formatter = new PrefixLogFormatter();
		Logger logger = createLogger(output, formatter, JavaUtil.I.getMethodName());
		formatter.withShowLogLevel(false).withShowLoggerName(true).withShowDate(false);
		/*
		 * Log some messages
		 */
		logger.info("First line");
		logger.info("No final newline...");
		logger.info("One unix newline...\nTwo unix newlines...\n\nOne final unix newline...\n");
		logger.info("Two final unix newlines...\n\n");
		logger.info("One windows newline...\r\nTwo windows newlines...\r\n\r\nOne final windows newline...\r\n");
		logger.info("Two final windows newlines...\r\n\r\n");
		logger.info("Last line");
		/*
		 * Verify the output preserves all newlines
		 */
		logger.getHandlers()[0].flush();
		String outputString = new String(output.toByteArray());
		String[] outputLines = UNIVERSAL_NEWLINE.split(outputString, -1);
		int i = 0;
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" First line", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" No final newline...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" One unix newline...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" Two unix newlines...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" One final unix newline...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" Two final unix newlines...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" One windows newline...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" Two windows newlines...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" One final windows newline...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" Two final windows newlines...", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" ", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" Last line", outputLines[i++]);
	}

	/**
	 * Verifies that message parameters are properly applied to a message format
	 * string
	 */
	@Test
	public void testFormattedLogMessage() throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrefixLogFormatter formatter = new PrefixLogFormatter();
		Logger logger = createLogger(output, formatter, JavaUtil.I.getMethodName());
		logger.log(Level.INFO, "Testing single variable replacement {0}", "one");
		logger.log(Level.INFO, "Testing multiple variable replacement {0} {1} {2} {3} {4}",
				new Object[] {"one", "two", "three", "four", "five"});

		logger.getHandlers()[0].flush();
		String outputString = new String(output.toByteArray());
		String[] outputLines = UNIVERSAL_NEWLINE.split(outputString, -1);
		int i = 0;
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" Testing single variable replacement one", outputLines[i++]);
		assertMatchesPattern(GENERIC_HEADER_PATTERN+" Testing multiple variable replacement one two three four five", outputLines[i++]);
	}

	
	private static void assertMatchesPattern(String regex, String message) {
		Assert.assertTrue(String.format("Pattern '%s' does not match '%s'", regex, message), Pattern.matches(regex, message));
	}
	
	private static Logger createLogger(OutputStream output, Formatter formatter, String loggerName) {
		StreamHandler handler = new StreamHandler(output, formatter);
		handler.setLevel(Level.ALL);
		handler.setFormatter(formatter);
		Logger logger = Logger.getLogger(loggerName);
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		return logger;
	}
}
