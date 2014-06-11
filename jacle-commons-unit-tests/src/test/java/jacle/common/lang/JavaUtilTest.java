package jacle.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jacle.common.lang.JavaUtil;

import java.util.regex.Pattern;

import org.junit.Test;

public class JavaUtilTest {
	
	/**
	 * Verifies that the {@link JavaUtil#getClassName()} method returns a proper
	 * value from the following contexts:
	 * <ul>
	 * <li>Static method</li>
	 * <li>Instance method</li>
	 * <li>Nested instance method</li>
	 * <li>Anonymous inner class method</li>
	 * </ul>
	 */
	@Test
	public void testGetClassName() throws Exception {
		JavaUtil util = JavaUtil.I;
		MockAltInterface anon = new MockAltInterface(){
			public String getFromAnonymousClass(JavaUtil util) {
				return util.getClassName();
			}
		};
		assertEquals(MockClassNameGetter.class.getName(), MockClassNameGetter.getFromStatic(util));
		assertEquals(MockClassNameGetter.class.getName(), new MockClassNameGetter().getFromMemberMethod(util));
		assertEquals(MockClassNameGetter.class.getName(), new MockClassNameGetter().getFromNestedMethod(util));
		// Matches string like "com.package.Class$1"
		Pattern expectedClassName = Pattern.compile(Pattern.quote(this.getClass().getName())+"\\$\\d+");
		assertTrue(expectedClassName.matcher(anon.getFromAnonymousClass(util)).matches());
	}
	
	static class MockClassNameGetter {

		public static String getFromStatic(JavaUtil util) {
			return util.getClassName();
		}

		public String getFromMemberMethod(JavaUtil util) {
			return util.getClassName();
		}

		public String getFromNestedMethod(JavaUtil util) {
			return nestedMethod(util);
		}

		private String nestedMethod(JavaUtil util) {
			return util.getClassName();
		}
	}

	/**
	 * Verifies that the {@link JavaUtil#getMethodName()} method returns a proper
	 * value from the following contexts:
	 * <ul>
	 * <li>Static method</li>
	 * <li>Instance method</li>
	 * <li>Nested instance method</li>
	 * <li>Anonymous inner class method</li>
	 * </ul>
	 */
	@Test
	public void testGetMethodName() throws Exception {
		JavaUtil util = JavaUtil.I;
		MockAltInterface anon = new MockAltInterface(){
			public String getFromAnonymousClass(JavaUtil util) {
				return util.getMethodName();
			}
		};
		assertEquals("getFromStatic", MockMethodNameGetter.getFromStatic(util));
		assertEquals("getFromMemberMethod", new MockMethodNameGetter().getFromMemberMethod(util));
		assertEquals("nestedMethod", new MockMethodNameGetter().getFromNestedMethod(util));
		assertEquals("getFromAnonymousClass", anon.getFromAnonymousClass(util));
	}
	
	static class MockMethodNameGetter {

		public static String getFromStatic(JavaUtil util) {
			return util.getMethodName();
		}

		public String getFromMemberMethod(JavaUtil util) {
			return util.getMethodName();
		}

		public String getFromNestedMethod(JavaUtil util) {
			return nestedMethod(util);
		}

		private String nestedMethod(JavaUtil util) {
			return util.getMethodName();
		}
	}

	/**
	 * Verifies that the {@link JavaUtil#getSimpleClassName()} method returns a
	 * proper value from the following contexts:
	 * <ul>
	 * <li>Static method</li>
	 * <li>Instance method</li>
	 * <li>Nested instance method</li>
	 * <li>Anonymous inner class method</li>
	 * </ul>
	 */
	@Test
	public void testGetSimpleClassName() {
		JavaUtil util = JavaUtil.I;
		MockAltInterface anon = new MockAltInterface(){
			public String getFromAnonymousClass(JavaUtil util) {
				return util.getSimpleClassName();
			}
		};
		assertEquals("JavaUtilTest$MockSimpleClassNameGetter", MockSimpleClassNameGetter.getFromStatic(util));
		assertEquals("JavaUtilTest$MockSimpleClassNameGetter", new MockSimpleClassNameGetter().getFromMemberMethod(util));
		assertEquals("JavaUtilTest$MockSimpleClassNameGetter", new MockSimpleClassNameGetter().getFromNestedMethod(util));
		// Matches string like "ClassName$1"
		Pattern expectedClassName = Pattern.compile(Pattern.quote(this.getClass().getSimpleName())+"\\$\\d+");
		assertTrue(expectedClassName.matcher(anon.getFromAnonymousClass(util)).matches());
	}

	private static class MockSimpleClassNameGetter {

		public static String getFromStatic(JavaUtil util) {
			return util.getSimpleClassName();
		}

		public String getFromMemberMethod(JavaUtil util) {
			return util.getSimpleClassName();
		}

		public String getFromNestedMethod(JavaUtil util) {
			return nestedMethod(util);
		}

		private String nestedMethod(JavaUtil util) {
			return util.getSimpleClassName();
		}
	}

	/**
	 * Verifies that the {@link JavaUtil#getSimpleClassName(String)} method
	 * returns a proper value from different name formats
	 */
	@Test
	public void testGetSimpleClassName_FromString() {
		JavaUtil util = JavaUtil.I;
		// Standard format
		assertEquals("SomeClass", util.getSimpleClassName("com.scea.SomeClass"));
		// Already simple
		assertEquals("SomeClass", util.getSimpleClassName("SomeClass"));
		// Includes numbers and symbols
		assertEquals("Some_Class9", util.getSimpleClassName("com.scea.number9.symbol_.Some_Class9"));
		// Not a valid class name
		assertEquals("some random invalid string", util.getSimpleClassName("some random invalid string"));
		// Named inner class
		assertEquals("WrapperClass$SomeClass", util.getSimpleClassName("com.scea.WrapperClass$SomeClass"));
		// Anonymous inner class
		assertEquals("WrapperClass$1", util.getSimpleClassName("com.scea.WrapperClass$1"));
	}

	private static interface MockAltInterface {
		public String getFromAnonymousClass(JavaUtil util);
	}
}
