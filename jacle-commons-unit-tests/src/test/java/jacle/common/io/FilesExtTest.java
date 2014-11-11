package jacle.common.io;

import static org.junit.Assert.assertEquals;
import jacle.commontest.JUnitFiles;

import java.io.File;
import java.nio.charset.StandardCharsets;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class FilesExtTest {

	private static JUnitFiles files = new JUnitFiles(); 
	
	@Before
	public void setUp() throws Exception {
		files.before();
	}
	
	/**
	 * Verifies that we can get the relative path of a file nested in a
	 * directory.
	 */
	@Test
	public void testGetRelativePath_SubFile() throws Exception {
		File dir = files.getFile("some/base/path");
		File file = files.getFile("some/base/path/some/sub/path");
		FilesExt.createParentDirs(file);
		FilesExt.write("mock-data", file, StandardCharsets.UTF_8);
		String relativePath = FilesExt.getRelativePath(dir, file);
		assertEquals("some/sub/path", relativePath);
	}
	
	/**
	 * Verifies that we can get the relative path of a directory nested in a
	 * directory.
	 */
	@Test
	public void testGetRelativePath_SubDir() throws Exception {
		File dir = files.getFile("some/base/path");
		File file = files.getFile("some/base/path/some/sub/path");
		FilesExt.mkdirs(file);
		String relativePath = FilesExt.getRelativePath(dir, file);
		assertEquals("some/sub/path", relativePath);
	}

	/**
	 * Verifies that we can get the relative path of a directory nested in a
	 * directory.
	 */
	@Test
	public void testGetRelativePath_NotInDir() throws Exception {
		File dir = files.getFile("some/base/path");
		File file = files.getFile("some/other/base/path/some/sub/path");
		FilesExt.mkdirs(file);
		try {
			FilesExt.getRelativePath(dir, file);
			Assert.fail("Expected exception");
		} catch (FileNotContainedInException e) {
			// Success
		}
	}

	/**
	 * Verifies that we can get the relative path of a file nested in a
	 * directory, when the two paths exist, and are specified differently and
	 * non-canonically (with ".." and/or ".").
	 */
	@Test
	public void testGetRelativePath_Existing_WithDifferentDots() throws Exception {
		File dir = files.getFile("some/base/path");
		File file = files.getFile("some/base/path/some/sub/path");
		FilesExt.createParentDirs(file);
		FilesExt.write("mock-data", file, StandardCharsets.UTF_8);
		dir = files.getFile("some/../some/base/./path");
		file = files.getFile("some/base/../base/path/some/../some/sub/path");
		String relativePath = FilesExt.getRelativePath(dir, file);
		assertEquals("some/sub/path", relativePath);
	}

	/**
	 * Verifies that we can get the relative path of a file nested in a
	 * directory, when the two paths exist, and are specified differently--one
	 * relative one absolute.
	 */
	@Test
	public void testGetRelativePath_Existing_RelativeAndAbsolute() throws Exception {
		File dir = files.getFile("some/base/path");
		File file = files.getFile("some/base/path/some/sub/path").getAbsoluteFile();
		FilesExt.createParentDirs(file);
		FilesExt.write("mock-data", file, StandardCharsets.UTF_8);
		dir = files.getFile("some/../some/base/./path");
		file = files.getFile("some/base/../base/path/some/../some/sub/path");
		String relativePath = FilesExt.getRelativePath(dir, file);
		assertEquals("some/sub/path", relativePath);
	}

	/**
	 * Verifies that we can get the relative path of a file nested in a
	 * directory, when the two don't exist, and are both specified relatively.
	 */
	@Test
	public void testGetRelativePath_NonExisting_Relative_NoDots() throws Exception {
		File dir = files.getFile("some/base/path");
		File file = files.getFile("some/base/path/some/sub/path");
		String relativePath = FilesExt.getRelativePath(dir, file);
		assertEquals("some/sub/path", relativePath);
	}

	/**
	 * Verifies that we can get the relative path of a file nested in a
	 * directory, when the two don't exist, and are both specified absolutely.
	 */
	@Test
	public void testGetRelativePath_NonExisting_Absolute_NoDots() throws Exception {
		File dir = files.getFile("some/base/path").getAbsoluteFile();
		File file = files.getFile("some/base/path/some/sub/path").getAbsoluteFile();
		String relativePath = FilesExt.getRelativePath(dir, file);
		assertEquals("some/sub/path", relativePath);
	}

	/**
	 * Verifies that we get a {@link FileNotFoundException} can't get the
	 * relative path of a file nested in a directory, when the two don't exist,
	 * and are specified differently--one relative one absolute.
	 */
	@Test
	public void testGetRelativePath_NonExisting_RelativeAndAbsolute() throws Exception {
		File dir = files.getFile("some/base/path");
		File file = files.getFile("some/base/path/some/sub/path").getAbsoluteFile();
		try {
			FilesExt.getRelativePath(dir, file);
			Assert.fail("Expected exception");
		} catch (FileNotFoundException e) {
			// Success
		}
	}

	/**
	 * Verifies that we get a {@link FileNotFoundException} can't get the
	 * relative path of a file nested in a directory, when the two don't exist,
	 * and are specified differently--both with non-canonical dots (".." or
	 * ".").
	 */
	@Test
	public void testGetRelativePath_NonExisting_DifferingDotPaths() throws Exception {
		File dir = files.getFile("some/../some/base/./path");
		File file = files.getFile("some/other/base/../base/path/some/../some/sub/path");
		try {
			FilesExt.getRelativePath(dir, file);
			Assert.fail("Expected exception");
		} catch (FileNotFoundException e) {
			// Success
		}
	}

	/**
	 * Verifies that we get {@link FileNotContainedInException} if the specified
	 * path is identical for both (the dir doesn't contain the file, it IS the
	 * file).
	 */
	@Test
	public void testGetRelativePath_Existing_IdenticalPaths() throws Exception {
		File dir = files.getFile("some/path");
		File file = files.getFile("some/path");
		FilesExt.mkdirs(file);
		try {
			FilesExt.getRelativePath(dir, file);
			Assert.fail("Expected exception");
		} catch (FileNotContainedInException e) {
			// Success
		}
	}
	

	/**
	 * Verifies that we get {@link FileNotContainedInException} if the specified
	 * path is identical for both (the dir doesn't contain the file, it IS the
	 * file).
	 */
	@Test
	public void testGetRelativePath_NonExisting_IdenticalPaths() throws Exception {
		File dir = files.getFile("some/path");
		File file = files.getFile("some/path");
		try {
			FilesExt.getRelativePath(dir, file);
			Assert.fail("Expected exception");
		} catch (FileNotContainedInException e) {
			// Success
		}
	}
}
