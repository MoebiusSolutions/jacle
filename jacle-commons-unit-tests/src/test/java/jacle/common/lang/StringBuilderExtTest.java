package jacle.common.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringBuilderExtTest {

	/**
	 * Verifies that appending objects serializes the objects properly, and that
	 * the resulting string is not interpreted with formatting
	 */
	@Test
	public void testAppendObject() {
		Object obj = new Object() {
			public String toString() { return "object-as-string-%n"; }
		};
		StringBuilderExt builder = new StringBuilderExt();
		builder.append(obj);
		builder.append(obj);
		assertEquals("object-as-string-%nobject-as-string-%n", builder.toString());
	}

	/**
	 * Verifies that format strings are properly appended
	 */
	@Test
	public void testAppendF_WithArgs() {
		StringBuilderExt builder = new StringBuilderExt();
		builder.appendF("Value: %s Value: %s%n", 1, "two");
		builder.appendF("Value: %s Value: %s", 3, "four");
		assertEquals("Value: 1 Value: two"+System.lineSeparator()+"Value: 3 Value: four", builder.toString());
	}

	/**
	 * Verifies that appending a no-arg string still causes the string to be
	 * treated as a format string
	 */
	@Test
	public void testAppendF_NoArgs() {
		StringBuilderExt builder = new StringBuilderExt();
		builder.appendF("This%nincludes%nnewlines%n");
		String expected = "This%nincludes%nnewlines%n".replace("%n", System.lineSeparator());
		assertEquals(expected, builder.toString());
	}

	/**
	 * Verifies that any format specifiers in the format arguments are not
	 * themselves interpreted as formatting specifiers
	 */
	@Test
	public void testAppendF_NestedFormatStrings() {
		Object obj = new Object() {
			public String toString() { return "object-value-%n"; }
		};
		StringBuilderExt builder = new StringBuilderExt();
		builder.appendF("%s %s", obj, "string-value-%n");
		assertEquals("object-value-%n string-value-%n", builder.toString());
	}


	/**
	 * Verifies that {@link StringBuilderExt#appendNL(Object)} properly injects
	 * newlines and does not interpret any provided strings as formatting
	 */
	@Test
	public void testAppendNL() {
		String NL = System.lineSeparator();
		Object obj = new Object() {
			public String toString() { return "object-value-%n"; }
		};
		StringBuilderExt builder = new StringBuilderExt();
		builder.appendNL(obj);
		builder.appendNL("string-value");
		builder.appendNL("string-value-%n");
		assertEquals("object-value-%n"+NL+"string-value"+NL+"string-value-%n"+NL, builder.toString());
	}
}
