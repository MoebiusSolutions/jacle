package jacle.common.io;

import java.io.File;
import java.io.IOException;

/**
 * Provides extensions to {@link com.google.common.io.Files}
 * 
 * @author rkenney
 */
public class FilesExt {

	/**
	 * Deletes a provided directory and all files contained within. Slightly
	 * different than Guava's deprecated implementation of this method, in that
	 * this does not throw an exception if the target dir already does not
	 * exist.</p>
	 * 
	 * Note that Guava deprecated this method with the following
	 * explanation:</p>
	 * 
	 * This method suffers from poor symlink detection and race conditions. This
	 * functionality can be supported suitably only by shelling out to an
	 * operating system command such as rm -rf or del /s. This method is
	 * scheduled to be removed in Guava release 11. Deletes all the files within
	 * a directory. Does not delete the directory itself. If the file argument
	 * is a symbolic link or there is a symbolic link in the path leading to the
	 * directory, this method will do nothing. Symbolic links within the
	 * directory are not followed.
	 **/
	public static void deleteDirectoryContents(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}
		if (!directory.isDirectory()) {
			throw new IOException(String.format("[%s] is not a directory", directory));
		}
		// Symbolic links will have different canonical and absolute paths
		if (!directory.getCanonicalPath().equals(directory.getAbsolutePath())) {
			return;
		}
		File[] files = directory.listFiles();
		if (files == null) {
			throw new IOException("Error listing files for " + directory);
		}
		for (File file : files) {
			deleteRecursively(file);
		}
	}

	/**
	 * Deletes a provided file or directory. If the provided path is a
	 * directory, this behaves identically to
	 * {@link #deleteDirectoryContents(File)}. Slightly different than Guava's
	 * deprecated implementation of this method, in that this does not throw an
	 * exception if the target file already does not exist.</p>
	 * 
	 * Note that Guava deprecated this method with the following
	 * explanation:</p>
	 * 
	 * This method suffers from poor symlink detection and race conditions. This
	 * functionality can be supported suitably only by shelling out to an
	 * operating system command such as rm -rf or del /s. This method is
	 * scheduled to be removed in Guava release 11. Deletes all the files within
	 * a directory. Does not delete the directory itself. If the file argument
	 * is a symbolic link or there is a symbolic link in the path leading to the
	 * directory, this method will do nothing. Symbolic links within the
	 * directory are not followed.
	 **/
	public static void deleteRecursively(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			deleteDirectoryContents(file);
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}

	/**
	 * Deletes a file, throwing an exception if this fails. Does not throw an
	 * exception if the file already doesn't exist.
	 * 
	 * @throws RuntimeIOException
	 */
	public static void delete(File file) throws RuntimeIOException {
		if (!file.exists()) {
			return;
		}
		if (!file.delete()) {
			throw new RuntimeIOException(String.format("Failed to delete file [%s]", file));
		}
	}
}
