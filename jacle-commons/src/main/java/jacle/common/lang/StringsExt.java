package jacle.common.lang;

import jacle.common.io.CloseablesExt;
import jacle.common.io.RuntimeIOException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;


/**
 * Provides extensions to {@link com.google.common.base.Strings}
 * 
 * @author rkenney
 */
public class StringsExt {

	private static final Pattern UNIVERSAL_NEWLINE = Pattern.compile("\r?\n");

	/**
	 * Static accessor
	 */
	public static final StringsExt I = new StringsExt();

	/**
	 * Similar to {@link Strings#isNullOrEmpty(String)}, but also returns true
	 * if the string contains only whitespace, as identified by
	 * {@link Character#isWhitespace(char)}.
	 */
	public boolean isNullOrBlank(String string) {
		if (Strings.isNullOrEmpty(string)) {
			return true;
		}
		for (int i = 0; i < string.length(); i++) {
			if (!Character.isWhitespace(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * <p>
	 * Gets a substring from the specified String avoiding exceptions.
	 * </p>
	 * 
	 * <p>
	 * A negative start position can be used to start {@code n} characters from
	 * the end of the String.
	 * </p>
	 * 
	 * <p>
	 * A {@code null} String will return {@code null}. An empty ("") String will
	 * return "".
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.substring(null, *)   = null
	 * StringUtils.substring("", *)     = ""
	 * StringUtils.substring("abc", 0)  = "abc"
	 * StringUtils.substring("abc", 2)  = "c"
	 * StringUtils.substring("abc", 4)  = ""
	 * StringUtils.substring("abc", -2) = "bc"
	 * StringUtils.substring("abc", -4) = "abc"
	 * </pre>
	 * 
	 * (copied from Apache Commons)
	 * 
	 * @param str
	 *            the String to get the substring from, may be null
	 * @param start
	 *            the position to start from, negative means count back from the
	 *            end of the String by this many characters
	 * @return substring from start position, {@code null} if null String input
	 */
	public String substring(String str, int start) {
		if (str == null) {
			return null;
		}
		// handle negatives, which means last n characters
		if (start < 0) {
			start = str.length() + start; // remember start is negative
		}
		if (start < 0) {
			start = 0;
		}
		if (start > str.length()) {
			return "";
		}
		return str.substring(start);
	}
	
    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start/end {@code n}
     * characters from the end of the String.</p>
     *
     * <p>The returned substring starts with the character in the {@code start}
     * position and ends before the {@code end} position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * {@code start = 0}. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     *
     * <p>If {@code start} is not strictly to the left of {@code end}, ""
     * is returned.</p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
	 * 
	 * (copied from Apache Commons)
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position to end position,
     *  {@code null} if null String input
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

	/**
	 * Writes the string to the provided stream. Does not close the provided
	 * stream.
	 */
	public void toStream(String string, OutputStream output, Charset charset) throws RuntimeIOException {
		ByteArrayInputStream input = new ByteArrayInputStream(string.getBytes(charset));
		try {
			ByteStreams.copy(input, output);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		} finally {
			CloseablesExt.closeQuietly(input);
		}
	}

	/**
	 * Reads the stream fully, and returns the content as a string. Closes the
	 * provided stream whether successful or not.
	 */
	public String fromStream(InputStream input, Charset charset) throws RuntimeIOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
		try {
			return CharStreams.toString(reader);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		} finally {
			CloseablesExt.closeQuietly(reader);
			CloseablesExt.closeQuietly(input);
		}
	}

	/**
	 * Compares two strings, using {@link String#compareTo(String)}, but
	 * handling null values as well. If both objects are null, they are compared
	 * equal. If one is null and the other isn't, the null value is sorted
	 * before the non-null value.
	 */
	public static int compare(String s1, String s2) {
		// Sort null to the beginning
		if (s1 == null) {
			return -1;
		} else if (s2 == null) {
			return 1;
		} else {
			return s1.compareTo(s2);
		}
	}

	/**
	 * Compares two strings, using {@link String#compareToIgnoreCase(String)}, but
	 * handling null values as well. If both objects are null, they are compared
	 * equal. If one is null and the other isn't, the null value is sorted
	 * before the non-null value.
	 */
	public static int compareIgnoreCase(String s1, String s2) {
		// Sort null to the beginning
		if (s1 == null) {
			return -1;
		} else if (s2 == null) {
			return 1;
		} else {
			return s1.compareToIgnoreCase(s2);
		}
	}

	/**
	 * Prefixes each line of the provided text with the provided string. Lines
	 * are interpreted as segments separated by Unix or Windows newlines. The
	 * resulting string replaces all Unix/Windows newlines with the newlines of
	 * the current OS.
	 */
	public String prefixLines(String text, String linePrefix) {
		// NOTE: -1 preserves trailing empty lines
		String[] lines = UNIVERSAL_NEWLINE.split(text, -1);
		StringBuilder result = new StringBuilder();
		String nextNewline = "";
		for (String line : lines) {
			result.append(nextNewline).append(linePrefix).append(line);
			nextNewline = System.lineSeparator();
		}
		return result.toString();
	}
}
