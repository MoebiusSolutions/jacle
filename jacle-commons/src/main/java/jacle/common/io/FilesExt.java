package jacle.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.io.Files;

/**
 * Provides extensions to {@link com.google.common.io.Files}
 * 
 * @author rkenney
 */
public class FilesExt {

	/**
	 * Deletes all files and directories within the provided directory. This is slightly
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
	 * 
	 * @throws RuntimeIOException
	 **/
	public static void deleteDirectoryContents(File directory) throws RuntimeIOException {
		try {
			if (!directory.exists()) {
				return;
			}
			if (!directory.isDirectory()) {
				throw new RuntimeIOException(String.format("[%s] is not a directory", directory));
			}
			// Symbolic links will have different canonical and absolute paths
			if (!getCanonicalFile(directory).equals(directory.getAbsolutePath())) {
				return;
			}
			File[] files = directory.listFiles();
			if (files == null) {
				throw new RuntimeIOException(String.format("Error listing files for [%s]", directory));
			}
			for (File file : files) {
				deleteRecursively(file);
			}
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to delete [%s] recursively", directory));
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
	 * 
	 * @throws RuntimeIOException
	 **/
	public static void deleteRecursively(File file) throws RuntimeIOException {
		boolean deleted;
		try {
			if (!file.exists()) {
				return;
			}
			if (file.isDirectory()) {
				deleteDirectoryContents(file);
			}
			deleted = file.delete();
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to delete [%s] recursively", file));
		}
		if (!deleted) {
			throw new RuntimeIOException(String.format("Failed to delete [%s] recursively", file));
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

	/**
	 * Creates the specific directory and all of its parents. Does not throw an
	 * exception if the file already doesn't exist.
	 * 
	 * @throws RuntimeIOException
	 */
	public static void mkdirs(File dir) throws RuntimeIOException {
		try {
			if (dir.exists()) {
				if (dir.isDirectory()) {
					return;
				} else {
					throw new RuntimeIOException(String.format("Directory [%s] exists as a file. Cannot create it.", dir));
				}
			}
			if (!dir.mkdirs()) {
				throw new RuntimeIOException(String.format("Directory [%s] exists as a file. Cannot create it.", dir));
			}
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to mkdirs to [%s]", dir), e); 
		}
			
	}

	/**
	 * Identical to {@link File#getCanonicalPath()}, but wraps the
	 * {@link IOException} in a descriptive {@link RuntimeIOException}.
	 * 
	 * @throws RuntimeIOException
	 */
	public static String getCanonicalPath(File file) throws RuntimeIOException {
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to get canonical path of [%s]", file), e);
		}
	}
	
	/**
	 * Identical to {@link File#getCanonicalFile()}, but wraps the
	 * {@link IOException} in a descriptive {@link RuntimeIOException}.
	 * 
	 * @throws RuntimeIOException
	 */
	public static File getCanonicalFile(File file) throws RuntimeIOException {
		try {
			return file.getCanonicalFile();
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to get canonical path of [%s]", file), e);
		}
	}
	
	/**
	 * Identical to {@link Files#createParentDirs(File)}, but wraps the
	 * {@link IOException} in a {@link RuntimeIOException}.
	 * 
	 * @throws RuntimeIOException
	 */
	public static void createParentDirs(File file) throws RuntimeIOException {
		try {
			Files.createParentDirs(file);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	/**
	 * Opens a {@link FileInputStream} from the file, throwing a description exception if this fails.
	 * 
	 * @throws RuntimeIOException
	 */
	public static FileInputStream newInputStream(File file) throws RuntimeIOException {
		try {
			return new FileInputStream(file);
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to open input stream from [%s].", file), e);
		} 
	}

	/**
	 * Opens a {@link FileOutputStream} to the file, throwing a description exception if this fails.
	 * 
	 * @throws RuntimeIOException
	 */
	public static FileOutputStream newOutputStream(File file) throws RuntimeIOException {
		try {
			return new FileOutputStream(file);
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to open output stream to [%s].", file), e);
		} 
	}

	/**
	 * Identical to {@link FilesExt#write(CharSequence, File, Charset)}, but wraps the
	 * {@link IOException} in a descriptive {@link RuntimeIOException}.
	 * 
	 *  @throws RuntimeIOException
	 */
	public static void write(CharSequence chars, File file, Charset charset) throws RuntimeIOException {
		try {
			Files.write(chars, file, charset);
		} catch (IOException e) {
			throw new RuntimeIOException(String.format("Failed to write to [%s]", file));
		}
	}
}
