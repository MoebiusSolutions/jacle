package jacle.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class HasEnumAltNameTest {

	@Test
	public void testGet_Success() {
		FirstEnum first = HasEnumAltName.get(FirstEnum.class, "aaa");
		assertTrue(FirstEnum.A.equals(first));
		assertFalse(FirstEnum.B.equals(first));
		assertFalse(SecondEnum.A.equals(first));
		assertFalse(SecondEnum.B.equals(first));
		SecondEnum second = HasEnumAltName.get(SecondEnum.class, "bbb");
		assertFalse(FirstEnum.A.equals(second));
		assertFalse(FirstEnum.B.equals(second));
		assertFalse(SecondEnum.A.equals(second));
		assertTrue(SecondEnum.B.equals(second));
	}

	@Test
	public void testGet_Null_Exception() {
		try {
			HasEnumAltName.get(FirstEnum.class, null);
			Assert.fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertEquals("No "+FirstEnum.class.getName()+" with alt-name of [null]", e.getMessage());
		}
	}

	@Test
	public void testGet_MissingValue_Exception() {
		try {
			HasEnumAltName.get(FirstEnum.class, "non-exist");
			Assert.fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertEquals("No "+FirstEnum.class.getName()+" with alt-name of [non-exist]", e.getMessage());
		}
	}

	@Test
	public void testGet_MissingInterface_Exception() {
		try {
			HasEnumAltName.get(MissingInterface.class, "aaa");
			Assert.fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertEquals("The enum type must implement "+HasEnumAltName.class.getName(), e.getMessage());
		}
	}

	@Test
	public void testGet_DuplicateAltName_Exception() {
		try {
			HasEnumAltName.get(DuplicateMapping.class, "aaa");
			Assert.fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertEquals("The enum type has duplicate alt-name entries for [aaa]", e.getMessage());
		}
	}

	@Test
	public void testGetArray_Success() {
		HasEnumAltName[] array = HasEnumAltName.getArray(FirstEnum.class, new String[]{"bbb", "aaa", "bbb"});
		assertEquals(3, array.length);
		assertEquals(FirstEnum.B, array[0]);
		assertEquals(FirstEnum.A, array[1]);
		assertEquals(FirstEnum.B, array[2]);
	}

	private static enum FirstEnum implements HasEnumAltName {
		A("aaa"),
		B("bbb");
		
		private String name;

		private FirstEnum(String name) {
			this.name = name;
		}
		
		@Override
		public String getAltName() {
			return name;
		}
		
	}

	private static enum SecondEnum implements HasEnumAltName {
		A("aaa"),
		B("bbb");
		
		private String name;

		private SecondEnum(String name) {
			this.name = name;
		}
		
		@Override
		public String getAltName() {
			return name;
		}
	}

	private static enum MissingInterface {
		A("aaa"),
		B("bbb");
		
		private String name;

		private MissingInterface(String name) {
			this.name = name;
		}
		
		@SuppressWarnings("unused")
		public String getAltName() {
			return name;
		}
	}

	private static enum DuplicateMapping implements HasEnumAltName {
		A("aaa"),
		B("bbb"),
		A_AGAIN("aaa");
		
		private String name;

		private DuplicateMapping(String name) {
			this.name = name;
		}
		
		@Override
		public String getAltName() {
			return name;
		}
	}
}
