package jacle.common.io.dir;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

public class FilesCopyMoveTest extends BaseFileTest {


    @Test
    public void testCopyFile() throws Exception {
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.copy(sourcePath,targetPath);
        assertThat(Files.size(targetPath), is(Files.size(sourcePath)));
        assertThat(Files.exists(sourcePath), is(true));
        assertThat(Files.exists(targetPath), is(true));
    }

    @Test
    public void testMoveFile() throws Exception {
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.move(sourcePath, targetPath);
        assertThat(Files.exists(sourcePath), is(false));
        assertThat(Files.exists(targetPath), is(true));
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testMoveFileNoFollowLinksInvalid() throws Exception {
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.move(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
    }

    
    @Test (expected = UnsupportedOperationException.class)
    public void testCopyInvalidOption() throws Exception{
        Path targetPath = dir1Path.resolve(basePath.relativize(sourcePath));
        Files.copy(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
    }


    @Test
    public void testCopyDirectory() throws Exception {
        Path target = basePath.resolve(copyDir);
        Path targetDir = target.resolve(basePath.relativize(fooPath));
        Files.copy(fooPath, targetDir);
        Path expectedPath = Paths.get(baseDir, copyDir, fooDir);
        assertThat(Files.exists(expectedPath), is(true));
    }

    @Test
    public void testMoveDirectory() throws Exception {
        Path tempPath = basePath.resolve(tempDir);
        Files.createDirectory(tempPath);
        Path target = basePath.resolve(Paths.get(copyDir));
        Path targetDir = target.resolve(basePath.relativize(tempPath));
        Files.move(tempPath, targetDir);
        assertThat(Files.notExists(tempPath), is(true));
        assertThat(Files.exists(basePath.resolve(Paths.get(copyDir, tempDir))), is(true));
    }

}
