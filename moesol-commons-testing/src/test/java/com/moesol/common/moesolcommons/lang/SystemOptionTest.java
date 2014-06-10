package com.moesol.common.moesolcommons.lang;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.moesol.common.moesolcommons.exec.JavaArgsBuilder;
import com.moesol.moesolcommonstest.JUnitFiles;


public class SystemOptionTest {

	/**
	 * Enable to spam tty with the output of child processes during development
	 */
	private static final boolean SHOW_DEBUG = false; 
	
	private static final JUnitFiles files = new JUnitFiles();
	
	@Before
	public void setUp() throws Exception {
		files.before();
	}
	
	@Test
	public void testGetString_OptionNotDefined_ReturnsDefault() throws Exception {
		File stdout = files.getFile("stdout.txt");
		File stderr = files.getFile("stderr.txt");
		String key = "my_mock_option";
		HashMap<String, String> env = new HashMap<String, String>();
		String[] cmd = new JavaArgsBuilder(SystemOptionMockProcess.class).setArgs(key, "not defined").build();
		executeProcess(cmd, stdout, stderr, env);
		assertEquals("not defined", SystemOptionMockProcess.readValueOutput(stdout, key));
	}

	@Test
	public void testGetString_DefinedAsJavaProperty() throws Exception {
		File stdout = files.getFile("stdout.txt");
		File stderr = files.getFile("stderr.txt");
		String key = "my_mock_option";
		HashMap<String, String> env = new HashMap<String, String>();
		String[] cmd = new JavaArgsBuilder(SystemOptionMockProcess.class).addJavaProperty(key, "from java property").setArgs(key, "not defined").build();
		executeProcess(cmd, stdout, stderr, env);
		assertEquals("from java property", SystemOptionMockProcess.readValueOutput(stdout, key));
	}

	@Test
	public void testGetString_DefinedAsEnvProperty() throws Exception {
		File stdout = files.getFile("stdout.txt");
		File stderr = files.getFile("stderr.txt");
		String key = "my_mock_option";
		HashMap<String, String> env = new HashMap<String, String>();
		env.put(key, "from environment");
		String[] cmd = new JavaArgsBuilder(SystemOptionMockProcess.class).setArgs(key, "not defined").build();
		executeProcess(cmd, stdout, stderr, env);
		assertEquals("from environment", SystemOptionMockProcess.readValueOutput(stdout, key));
	}

	@Test
	public void testGetString_DefinedAsJavaAndEnvProperties_ReturnsJavaProperty() throws Exception {
		File stdout = files.getFile("stdout.txt");
		File stderr = files.getFile("stderr.txt");
		String key = "my_mock_option";
		HashMap<String, String> env = new HashMap<String, String>();
		env.put(key, "from environment");
		String[] cmd = new JavaArgsBuilder(SystemOptionMockProcess.class).addJavaProperty(key, "from java property").setArgs(key, "not defined").build();
		executeProcess(cmd, stdout, stderr, env);
		assertEquals("from java property", SystemOptionMockProcess.readValueOutput(stdout, key));
	}

	@Test
	public void testGetString_DefinedAsJavaPropertyEmptyString_RetursEmptyString() throws Exception {
		File stdout = files.getFile("stdout.txt");
		File stderr = files.getFile("stderr.txt");
		String key = "my_mock_option";
		HashMap<String, String> env = new HashMap<String, String>();
		String[] cmd = new JavaArgsBuilder(SystemOptionMockProcess.class).addJavaProperty(key, "").setArgs(key, "not defined").build();
		executeProcess(cmd, stdout, stderr, env);
		assertEquals("", SystemOptionMockProcess.readValueOutput(stdout, key));
	}

	@Test
	public void testGetString_DefinedAsEnvPropertyEmptyString_RetursEmptyString() throws Exception {
		File stdout = files.getFile("stdout.txt");
		File stderr = files.getFile("stderr.txt");
		String key = "my_mock_option";
		HashMap<String, String> env = new HashMap<String, String>();
		env.put(key, "");
		String[] cmd = new JavaArgsBuilder(SystemOptionMockProcess.class).setArgs(key, "not defined").build();
		executeProcess(cmd, stdout, stderr, env);
		assertEquals("", SystemOptionMockProcess.readValueOutput(stdout, key));
	}

	private static void executeProcess(String[] cmd, File stdout, File stderr, Map<String,String> envValues) throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder(cmd).redirectOutput(stdout).redirectError(stderr);
		Map<String, String> procEnv = processBuilder.environment();
		for (Entry<String, String> envValue : envValues.entrySet()) {
			procEnv.put(envValue.getKey(), envValue.getValue());
		}
		Process process = processBuilder.start();
		int exit = process.waitFor();
		if (SHOW_DEBUG) {
			System.out.println(Files.toString(stdout, StandardCharsets.UTF_8));
			System.out.println(Files.toString(stderr, StandardCharsets.UTF_8));
		}
		Assert.assertEquals(0, exit);
	}
}
