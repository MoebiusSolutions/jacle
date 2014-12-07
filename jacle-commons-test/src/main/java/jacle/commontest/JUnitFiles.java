package jacle.commontest;

import jacle.common.io.FilesExt;
import jacle.common.io.RuntimeIOException;
import jacle.common.lang.JavaUtil;

import java.io.File;
import java.nio.file.Files;

public class JUnitFiles {
	
	private final File baseDir;

	/**
	 * Creates a temporary directory for files named for the class that
	 * instantiated this object
	 */
	public JUnitFiles() {
		this(JavaUtil.I.getClassName(1));
	}
	
	/**
	 * Creates a temporary directory for files named for the provided junit
	 * class
	 */
	public JUnitFiles(Class<?> junitClass) {
		this(junitClass.getName());
	}
	
	/**
	 * Creates a temporary directory for files named for the provided junit
	 * class
	 */
	public JUnitFiles(String junitClassName) {
		this(new File("target/junit-temp/"+junitClassName));
	}
	
	/**
	 * Creates a temporary directory for files in the specified directory
	 */
	public JUnitFiles(File dir) {
		baseDir = dir;
	}

	/**
	 * Empties/creates the directory. Synonym with {@link #setUp()}
	 */
	public void before() throws RuntimeIOException {
		setUp();
	}

	/**
	 * Empties/creates the directory
	 */
	public void setUp() throws RuntimeIOException {
		try {
			FilesExt.deleteDirectoryContents(baseDir);
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Filed to delete directory [%s]", baseDir), e);
		}
		try {
			Files.createDirectories(baseDir.toPath());
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Filed to create directory [%s]", baseDir), e);
		}
	}
	
	/**
	 * Return a {@link File} path within the temporary directory
	 */
	public File getFile(String relativePath) {
		return new File(baseDir, relativePath);
	}
}
