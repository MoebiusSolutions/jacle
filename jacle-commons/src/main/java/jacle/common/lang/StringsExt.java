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

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;


/**
 * Provides extensions to {@link com.google.common.base.Strings}
 * 
 * @author rkenney
 */
public class StringsExt {

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
}
