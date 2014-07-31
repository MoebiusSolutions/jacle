package jacle.commontest.junit;

import static org.junit.Assert.assertEquals;
import jacle.common.lang.JavaUtil;
import jacle.commontest.TempJavaProperty;

import org.junit.Test;

public class TempJavaPropertyTest {

	@Test
	public void testSetRest_InitiallyNull() throws Exception {
		// Verify initial state
		String propertyName = JavaUtil.I.getSimpleClassName()+"_"+JavaUtil.I.getMethodName()+"_NonExist";
		assertEquals(null, System.getProperty(propertyName));
		// Verify constructor has no effect
		TempJavaProperty tempProperty = new TempJavaProperty(propertyName, "new value");
		assertEquals(null, System.getProperty(propertyName));
		// Verify set has effect
		tempProperty.set();
		assertEquals("new value", System.getProperty(propertyName));
		// Verify reset has effect
		tempProperty.reset();
		assertEquals(null, System.getProperty(propertyName));
	}

	@Test
	public void testSetRest_InitiallySet() throws Exception {
		// Verify initial state
		String propertyName = JavaUtil.I.getSimpleClassName()+"_"+JavaUtil.I.getMethodName()+"_NonExist";
		System.setProperty(propertyName, "initial value");
		assertEquals("initial value", System.getProperty(propertyName));
		// Verify constructor has no effect
		TempJavaProperty tempProperty = new TempJavaProperty(propertyName, "new value");
		assertEquals("initial value", System.getProperty(propertyName));
		// Verify set has effect
		tempProperty.set();
		assertEquals("new value", System.getProperty(propertyName));
		// Verify reset has effect
		tempProperty.reset();
		assertEquals("initial value", System.getProperty(propertyName));
	}

	@Test
	public void testSetRest_SetToNull() throws Exception {
		// Verify initial state
		String propertyName = JavaUtil.I.getSimpleClassName()+"_"+JavaUtil.I.getMethodName()+"_NonExist";
		System.setProperty(propertyName, "initial value");
		assertEquals("initial value", System.getProperty(propertyName));
		// Verify constructor has no effect
		TempJavaProperty tempProperty = new TempJavaProperty(propertyName, null);
		assertEquals("initial value", System.getProperty(propertyName));
		// Verify set has effect
		tempProperty.set();
		assertEquals(null, System.getProperty(propertyName));
		// Verify reset has effect
		tempProperty.reset();
		assertEquals("initial value", System.getProperty(propertyName));
	}
}
