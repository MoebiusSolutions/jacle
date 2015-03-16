package jacle.common.lang;

import static org.junit.Assert.assertEquals;
import jacle.commontest.JUnitFiles;

import java.io.FileInputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class PropertiesUtilsTest {

	private static JUnitFiles files = new JUnitFiles();
	
	@Before
	public void setUp() throws Exception {
		files.before();
	}
	
	/**
	 * Verifies the ability to write/read file using
	 * {@link PropertiesUtils#toFile(Properties, java.io.File)} and
	 * {@link PropertiesUtils#fromFile(java.io.File)}
	 */
	@Test
	public void testToFromFile() throws Exception {
		Properties p = new Properties();
		p.setProperty("a", "X");
		p.setProperty("b", "Y");
		p.remove("b");
		
		// Write file
		PropertiesUtils.I.toFile(p, files.getFile("mock.properties"));
		
		// Verify written file
		p = PropertiesUtils.I.fromFile(files.getFile("mock.properties"));
		assertEquals("X", p.getProperty("a"));
		assertEquals(null, p.getProperty("b"));
	}

	/**
	 * Verifies the ability to read properties using
	 * {@link PropertiesUtils#fromStream(java.io.InputStream)}
	 */
	@Test
	public void testFromStream() throws Exception {
		// Setup
		Properties p = new Properties();
		p.setProperty("a", "X");
		p.setProperty("b", "Y");
		p.remove("b");
		PropertiesUtils.I.toFile(p, files.getFile("mock.properties"));

		// Read from stream
		FileInputStream stream = new FileInputStream(files.getFile("mock.properties"));
		try {
			p = PropertiesUtils.I.fromStream(stream);
		} finally {
			stream.close();
		}
		
		// Verify contents read
		assertEquals("X", p.getProperty("a"));
		assertEquals(null, p.getProperty("b"));
	}

}
