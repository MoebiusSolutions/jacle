package jacle.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.util.Set;

import com.google.common.io.Files;

/**
 * Provides extensions to {@link com.google.common.io.Files}
 * 
 * @author rkenney
 */
public class FilesExt {

	/**
	 * Deletes all files and directories within the provided directory. This is
	 * slightly different than Guava's deprecated implementation of this method,
	 * in that this does not throw an exception if the target dir already does
	 * not exist, and this has safer handling of symlinks by leveraging JDK
	 * 7.</p>
	 * 
	 * Note that Guava deprecated this method with the following
	 * explanation:</p>
	 * 
	 * "This method suffers from poor symlink detection and race conditions.
	 * This functionality can be supported suitably only by shelling out to an
	 * operating system command such as rm -rf or del /s. This method is
	 * scheduled to be removed in Guava release 11. Deletes all the files within
	 * a directory. Does not delete the directory itself. If the file argument
	 * is a symbolic link or there is a symbolic link in the path leading to the
	 * directory, this method will do nothing. Symbolic links within the
	 * directory are not followed."</p>
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
			// Do not walk into symlinks. Just stop, allowing the caller to
			// delete the link if desired.
			if (java.nio.file.Files.isSymbolicLink(directory.toPath())) {
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
	 * {@link #deleteDirectoryContents(File)}. This is slightly different than
	 * Guava's deprecated implementation of this method, in that this does not
	 * throw an exception if the target dir already does not exist, and this has
	 * safer handling of symlinks by leveraging JDK 7.</p>
	 * 
	 * Note that Guava deprecated this method with the following
	 * explanation:</p>
	 * 
	 * "This method suffers from poor symlink detection and race conditions.
	 * This functionality can be supported suitably only by shelling out to an
	 * operating system command such as rm -rf or del /s. This method is
	 * scheduled to be removed in Guava release 11. Deletes all the files within
	 * a directory. Does not delete the directory itself. If the file argument
	 * is a symbolic link or there is a symbolic link in the path leading to the
	 * directory, this method will do nothing. Symbolic links within the
	 * directory are not followed."
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
	 * Deletes the specified directory and any parent directories if and only if
	 * they are empty.
	 * 
	 * @throws RuntimeIOException
	 */
	public static void deleteEmptyDirAndParents(File directory) throws RuntimeIOException {
		try {
			if (!directory.isDirectory()) {
				return;
			}
			if (directory.list().length > 0) {
				return;
			}
			// Delete each parent directory that has no children
			while (directory.list().length < 1) {
				File parent = directory.getParentFile();
				delete(directory);
				directory = parent;
			}
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to delete empty parents of [%s]", directory), e);
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
				throw new RuntimeIOException(String.format("Failed to create directory [%s]", dir));
			}
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to mkdirs to [%s]", dir), e); 
		}
	}

	/**
	 * Identical to {@link Files#move(File, File)}, but wraps the
	 * {@link IOException} in a descriptive {@link RuntimeIOException}.
	 * 
	 * @throws RuntimeIOException
	 */
	public static void move(File file, File newFile) throws RuntimeIOException {
		try {
			Files.move(file, newFile);
		} catch (IOException e) {
			throw new RuntimeIOException(String.format("Failed to move [%s] to [%s]", file, newFile));
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
			throw new RuntimeIOException(String.format("Failed to create parent directories of [%s]", file), e);
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
			throw new RuntimeIOException(String.format("Failed to write to [%s]", file), e);
		}
	}

	/**
	 * Identical to {@link FilesExt#append(CharSequence, File, Charset)}, but wraps the
	 * {@link IOException} in a descriptive {@link RuntimeIOException}.
	 * 
	 *  @throws RuntimeIOException
	 */
	public static void append(CharSequence chars, File file, Charset charset) throws RuntimeIOException {
		try {
			Files.append(chars, file, charset);
		} catch (IOException e) {
			throw new RuntimeIOException(String.format("Failed to write to [%s]", file), e);
		}
	}

	/**
	 * Identical to {@link Files#toString(File, Charset)}, but wraps the
	 * {@link IOException} in a descriptive {@link RuntimeIOException}.
	 * 
	 * @throws RuntimeIOException
	 */
	public static String toString(File file, Charset charset) throws RuntimeIOException {
		try {
			return Files.toString(file, charset);
		} catch (IOException e) {
			throw new RuntimeIOException(String.format("Failed to read [%s]", file), e);
		}
	}

	/**
	 * Identical to
	 * {@link java.nio.file.Files#walkFileTree(Path, Set, int, FileVisitor)},
	 * but wraps the {@link IOException} in a descriptive
	 * {@link RuntimeIOException}.
	 * 
	 * @throws RuntimeIOException
	 */
    public static Path walkFileTree(
    		Path start, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor)
    				throws RuntimeIOException
    {
		try {
			return java.nio.file.Files.walkFileTree(start, options, maxDepth, visitor);
		} catch (IOException e) {
			throw new RuntimeIOException(String.format("Failed to walk files from [%s]", start), e);
		}
    }

	/**
	 * Identical to {@link java.nio.file.Files#walkFileTree(Path, FileVisitor)},
	 * but wraps the {@link IOException} in a descriptive
	 * {@link RuntimeIOException}.
	 * 
	 * @throws RuntimeIOException
	 */
	public static Path walkFileTree(Path start, FileVisitor<? super Path> visitor) throws RuntimeIOException {
		try {
			return java.nio.file.Files.walkFileTree(start, visitor);
		} catch (IOException e) {
			throw new RuntimeIOException(String.format("Failed to walk files from [%s]", start), e);
		}
	}

	/**
	 * Returns the relative file path from the <code>baseDir</code> to the
	 * <code>targetFile</code>. The two paths need not be in the same form
	 * (canonical, relative, absolute, etc). However, if they are not, they must
	 * both exist. Both paths must not point to the same directory. No "/"
	 * prefix is included in the return; it's a relative path.</p>
	 * 
	 * For example, if these parameters were provided:</p>
	 * 
	 * <ul>
	 * <li>baseDir: "some/base/path"</li>
	 * <li>targetDir: "some/base/path/some/sub/path"</li>
	 * </ul>
	 * 
	 * ... the method would return "some/sub/path".
	 * 
	 * This method first attempts to compare the strings of the two paths
	 * literally. This means that relative or non-canonical paths can be
	 * compared, and if the <code>baseDir</code> is an exact prefix of the
	 * <code>targetFile</code> path, a relative path will be returned (that may
	 * or may not contain ".." or ".").</p>
	 * 
	 * However, if the <code>baseDir</code> is not an exact prefix to the
	 * <code>targetFile</code>, both paths will be converted to their canonical
	 * forms for comparison. This requires that both files exist. If either does
	 * not exist, this method throws a {@link FileNotFoundException}. If both
	 * files exist, but the <code>targetFile</code> does not fall within the
	 * <code>baseDir</code>, this method throws a
	 * {@link FileNotContainedInException}. Otherwise, this method returns the
	 * relative path from the <code>baseDir</code> to the
	 * <code>targetFile</code>.
	 * 
	 * @throws FileNotContainedInException
	 *             Thrown if <code>targetFile</code> does not contain
	 *             <code>baseDir</code>. This exception is never thrown if this
	 *             can't be decided conclusively. This exception is thrown when
	 *             both paths point to the same file. E.g. the base path does
	 *             not include the target path, it IS the target path.</p>
	 * 
	 * @throws FileNotFoundException
	 *             Thrown if and only if the two paths (<code>baseDir</code> and
	 *             <code>targetFile</code>) are specified in different forms
	 *             (relative, absolute, non-canonical, etc) and one of them
	 *             doesn't exist. In this situation is is impossible to conclude
	 *             whether <code>baseDir</code> contains <code>targetFile</code>
	 *             . This exception is NOT thrown if both paths are the same
	 *             form (both absolute, for example), and either does not exist,
	 *             as simple textual comparison of the paths is still possible.
	 *             <p>
	 * 
	 * @throws RuntimeIOException
	 *             If anything else fails
	 */
	public static String getRelativePath(File baseDir, File targetFile) throws FileNotContainedInException, FileNotFoundException, RuntimeIOException {
		try {
			// Attempt literal match (file need not exist if base paths are identical)
			String basePath = baseDir.getPath();
			String targetPath = targetFile.getPath();
			if (targetPath.startsWith(basePath)) {
				if (basePath.length() == targetPath.length()) {
					throw new FileNotContainedInException(String.format("Base dir and target file are the same file [%s]", targetFile));
				}
				return targetPath.substring(basePath.length()+1 /* skip slash */);
			}
			// Convert paths to canonical form to attempt match
			if (!baseDir.exists() && !targetFile.exists()) {
				throw new FileNotFoundException(String.format(
						"Cannot confirm that target file [%s] is contained in the base dir [%s] as one doesn't exist", targetFile, baseDir));
			}
			basePath = baseDir.getCanonicalPath();
			targetPath = targetFile.getCanonicalPath();
			if (targetPath.startsWith(basePath)) {
				if (basePath.length() == targetPath.length()) {
					throw new FileNotContainedInException(String.format("Base dir and target file are the same file [%s]", targetFile));
				}
				return targetPath.substring(basePath.length()+1 /* skip slash */);
			}
			throw new FileNotContainedInException(String.format("Target file [%s] is not contained in the base dir [%s]", targetFile, baseDir));
		} catch (FileNotContainedInException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeIOException(String.format("Failed to get relative path of [%s] within [%s]", targetFile, baseDir));
		}
	}

	/**
	 * Returns the relative file path from the <code>baseDir</code> to the
	 * <code>targetFile</code>. The two paths need not be in the same form
	 * (canonical, relative, absolute, etc). However, if they are not, they must
	 * both exist. Both paths must not point to the same directory. No "/"
	 * prefix is included in the return; it's a relative path.</p>
	 * 
	 * For example, if these parameters were provided:</p>
	 * 
	 * <ul>
	 * <li>baseDir: "some/base/path"</li>
	 * <li>targetDir: "some/base/path/some/sub/path"</li>
	 * </ul>
	 * 
	 * ... the method would return "some/sub/path".
	 * 
	 * This method first attempts to compare the strings of the two paths
	 * literally. This means that relative or non-canonical paths can be
	 * compared, and if the <code>baseDir</code> is an exact prefix of the
	 * <code>targetFile</code> path, a relative path will be returned (that may
	 * or may not contain ".." or ".").</p>
	 * 
	 * However, if the <code>baseDir</code> is not an exact prefix to the
	 * <code>targetFile</code>, both paths will be converted to their canonical
	 * forms for comparison. This requires that both files exist. If either does
	 * not exist, this method throws a {@link FileNotFoundException}. If both
	 * files exist, but the <code>targetFile</code> does not fall within the
	 * <code>baseDir</code>, this method throws a
	 * {@link FileNotContainedInException}. Otherwise, this method returns the
	 * relative path from the <code>baseDir</code> to the
	 * <code>targetFile</code>.
	 * 
	 * @throws FileNotContainedInException
	 *             Thrown if <code>targetFile</code> does not contain
	 *             <code>baseDir</code>. This exception is never thrown if this
	 *             can't be decided conclusively. This exception is thrown when
	 *             both paths point to the same file. E.g. the base path does
	 *             not include the target path, it IS the target path.</p>
	 * 
	 * @throws FileNotFoundException
	 *             Thrown if and only if the two paths (<code>baseDir</code> and
	 *             <code>targetFile</code>) are specified in different forms
	 *             (relative, absolute, non-canonical, etc) and one of them
	 *             doesn't exist. In this situation is is impossible to conclude
	 *             whether <code>baseDir</code> contains <code>targetFile</code>
	 *             . This exception is NOT thrown if both paths are the same
	 *             form (both absolute, for example), and either does not exist,
	 *             as simple textual comparison of the paths is still possible.
	 *             <p>
	 * 
	 * @throws RuntimeIOException
	 *             If anything else fails
	 */
	public static File getRelativeFile(File baseDir, File targetFile) throws FileNotContainedInException, FileNotFoundException, RuntimeIOException {
		return new File(getRelativePath(baseDir, targetFile));
	}
}
