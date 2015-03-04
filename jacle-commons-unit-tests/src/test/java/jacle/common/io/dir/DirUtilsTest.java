package jacle.common.io.dir;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jacle.common.io.FilesExt;
import jacle.commontest.JUnitFiles;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class DirUtilsTest {

	private static final JUnitFiles files = new JUnitFiles();
    private Path sourcePath;
    private Path targetPath;
    private Path fooPath;
    private Path barPath;
    private Path bazPath;
    private Path file1;
    private Path file2;
    private Path file3;
    private Path file4;
    private Path fooPathTarget;
    private Path barPathTarget;
    private Path bazPathTarget;
    private Path file1Target;
    private Path file2Target;
    private Path file3Target;
    private Path file4Target;
    private int numLines;
    private Path[] filePaths;

    @Before
    public void setUp() throws Exception {
    	files.before();
        sourcePath = files.getFile("test-source-dir").toPath();
        targetPath = files.getFile("test-target-dir").toPath();
        setUpSourcePaths();
        setUpTargetPaths();
        filePaths = new Path[]{file1, file2, file3, file4};
        numLines = 5000;
        createTestDirectoryStructure();
    }

    @Test
    public void testDirUtilsClean() throws Exception {
        assertAllSourceFilesExistIs(true);
        assertAllSourceDirsExistIs(true);

        DirUtils.I.clean(sourcePath);
        assertAllSourceFilesExistIs(false);
        assertAllSourceDirsExistIs(true);
    }

    @Test
    public void testDirUtilsDelete() throws Exception {
        assertAllSourceFilesExistIs(true);
        assertAllSourceDirsExistIs(true);

        DirUtils.I.delete(sourcePath);
        assertAllSourceFilesExistIs(false);
        assertAllSourceDirsExistIs(false);
    }

    @Test
    public void testDirUtilsCopy() throws Exception {
        assertAllSourceDirsExistIs(true);
        assertAllSourceFilesExistIs(true);
        assertAllTargetDirsExistIs(false);
        assertAllTargetFilesExistIs(false);

        DirUtils.I.copy(sourcePath, targetPath);

        assertAllSourceDirsExistIs(true);
        assertAllSourceFilesExistIs(true);
        assertAllTargetDirsExistIs(true);
        assertAllTargetFilesExistIs(true);
    }

    @Test
    public void testDirUtilsMove() throws Exception {
        assertAllSourceDirsExistIs(true);
        assertAllSourceFilesExistIs(true);
        assertAllTargetDirsExistIs(false);
        assertAllTargetFilesExistIs(false);

        DirUtils.I.move(sourcePath, targetPath);

        assertAllSourceDirsExistIs(false);
        assertAllSourceFilesExistIs(false);
        assertAllTargetDirsExistIs(true);
        assertAllTargetFilesExistIs(true);
    }

    @Test
    public void testApply() throws Exception {
        final List<String> names = new ArrayList<>();
        Function<Path, FileVisitResult> function = new Function<Path, FileVisitResult>() {
            @Override
            public FileVisitResult apply(Path path) {
                names.add(path.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }
        };
        DirUtils.I.apply(sourcePath, function);
        assertThat(names.size(), is(4));
        assertThat(names.contains("file1.txt"), is(true));
        assertThat(names.contains("file2.txt"), is(true));
        assertThat(names.contains("file3.txt"), is(true));
        assertThat(names.contains("file4.txt"), is(true));
    }

    @Test
    public void testCopyPredicate() throws Exception {
        Predicate<Path> copyPredicate = new Predicate<Path>() {
            @Override
            public boolean apply(Path input) {
                return (Files.isDirectory(input) && !input.getFileName().toString().equals("foo"));
            }
        };
        DirUtils.I.copyWithPredicate(sourcePath, targetPath, copyPredicate);
        assertThat(Files.exists(targetPath), is(true));
        assertThat(Files.exists(fooPathTarget), is(false));
        assertThat(Files.exists(barPathTarget), is(false));
        assertThat(Files.exists(bazPathTarget), is(true));
    }

    @Test
    public void testDirectoryStream() throws Exception {
        int expectedCount = 4;
        int fileCount = 0;
        try (DirectoryStream<Path> directoryStream = DirUtils.I.glob(sourcePath, "*.txt")) {
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(expectedCount, is(fileCount));
    }


    //TODO add method to assert all files sizes are same

    private void assertAllSourceFilesExistIs(boolean flag) {
        assertThat(Files.exists(file1), is(flag));
        assertThat(Files.exists(file2), is(flag));
        assertThat(Files.exists(file3), is(flag));
        assertThat(Files.exists(file4), is(flag));
    }

    private void assertAllSourceDirsExistIs(boolean flag) {
        assertThat(Files.exists(sourcePath), is(flag));
        assertThat(Files.exists(fooPath), is(flag));
        assertThat(Files.exists(barPath), is(flag));
        assertThat(Files.exists(bazPath), is(flag));
    }

    private void assertAllTargetFilesExistIs(boolean flag) {
        assertThat(Files.exists(file1Target), is(flag));
        assertThat(Files.exists(file2Target), is(flag));
        assertThat(Files.exists(file3Target), is(flag));
        assertThat(Files.exists(file4Target), is(flag));
    }

    private void assertAllTargetDirsExistIs(boolean flag) {
        assertThat(Files.exists(targetPath), is(flag));
        assertThat(Files.exists(fooPathTarget), is(flag));
        assertThat(Files.exists(barPathTarget), is(flag));
        assertThat(Files.exists(bazPathTarget), is(flag));
    }

    private void createTestDirectoryStructure() throws Exception {
        Files.createDirectories(barPath);
        Files.createDirectories(bazPath);
        for (Path filePath : filePaths) {
            generateFile(filePath, numLines);
        }
    }

    private void generateFile(Path path, int numberLines) throws Exception {
    	FilesExt.delete(path.toFile());
    	for (int i=0; i<numberLines; i++) {
        	FilesExt.append("xxx\n", path.toFile(), StandardCharsets.UTF_8);
    	}
    }

    private void setUpTargetPaths() {
        fooPathTarget = targetPath.resolve("foo");
        barPathTarget = fooPathTarget.resolve("bar");
        bazPathTarget = targetPath.resolve("baz");
        file1Target = fooPathTarget.resolve("file1.txt");
        file2Target = barPathTarget.resolve("file2.txt");
        file3Target = barPathTarget.resolve("file3.txt");
        file4Target = bazPathTarget.resolve("file4.txt");
    }

    private void setUpSourcePaths() {
        fooPath = sourcePath.resolve("foo");
        barPath = fooPath.resolve("bar");
        bazPath = sourcePath.resolve("baz");
        file1 = fooPath.resolve("file1.txt");
        file2 = barPath.resolve("file2.txt");
        file3 = barPath.resolve("file3.txt");
        file4 = bazPath.resolve("file4.txt");
    }
}
