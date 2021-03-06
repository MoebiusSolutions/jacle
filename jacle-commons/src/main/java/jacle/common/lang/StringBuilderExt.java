package jacle.common.lang;

/**
 * Similar to {@link StringBuilder}, with advanced methods
 * 
 * @author rkenney
 */
public class StringBuilderExt {

	private final StringBuilder buffer = new StringBuilder();
	
	public StringBuilderExt() {}

	/**
	 * Appends an object to the buffer. Identical to
	 * {@link StringBuilder#append(Object)}.
	 * 
	 * @param object
	 *            The object to append
	 * 
	 * @return "this" (fluent setter)
	 */
	public StringBuilderExt append(Object object) {
		buffer.append(object);
		return this;
	}

	/**
	 * Appends a formatted string. Method syntax is identical to
	 * {@link String#format(String, Object...)}.
	 * 
	 * @param format
	 *            Format string
	 * @param args
	 *            Format string args
	 * 
	 * @return "this" (fluent setter)
	 */
	public StringBuilderExt appendF(String format, Object... args) {
		buffer.append(String.format(format, args));
		return this;
	}

	/**
	 * Appends a formatted string. Method syntax is identical to
	 * {@link String#format(String, Object...)}.
	 * 
	 * @param format
	 *            Format string (with no args)
	 * 
	 * @return "this" (fluent setter)
	 */
	public StringBuilderExt appendF(String format) {
		buffer.append(String.format(format));
		return this;
	}

	/**
	 * Appends an object to the buffer and then appends a system-specific line
	 * separator. Identical to {@link StringBuilder#append(Object)}.
	 */
	public void appendNL(Object object) {
		buffer.append(object).append(System.lineSeparator());
	}
	
	/** 
	 * @return The constructed string
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}
}
