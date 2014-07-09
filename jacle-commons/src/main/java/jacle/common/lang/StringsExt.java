package jacle.common.lang;

import com.google.common.base.Strings;


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
}
