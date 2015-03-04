package jacle.common.io.dir.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jacle.common.io.dir.BaseFileTest;
import jacle.common.io.dir.events.FileDirectoryStream;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class FileDirectoryStreamTest extends BaseFileTest {
    private int expectedJavaFileCount;
    private int expectedTotalFileCount;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        expectedJavaFileCount = fakeJavaFiles.length * 3;
        expectedTotalFileCount = expectedJavaFileCount + textFileNames.length + 1;
    }

    @Test
    public void testGlob() throws Exception {
        File baseDir = basePath.toFile();
        FileDirectoryStream directoryStream = new FileDirectoryStream(".*", baseDir);
        Iterator<File> fileIterator = directoryStream.glob();
        int fileCount = 0;
        while (fileIterator.hasNext()) {
            File f = fileIterator.next();
            if (f.isFile()) {
                fileCount++;
            }
        }
        directoryStream.close();
        assertThat(fileCount, is(expectedTotalFileCount));
    }

    @Test
    public void testGlobJavaFiles() throws Exception {
        File baseDir = basePath.toFile();
        FileDirectoryStream directoryStream = new FileDirectoryStream(".*\\.java$", baseDir);
        Iterator<File> fileIterator = directoryStream.glob();
        int fileCount = 0;
        while (fileIterator.hasNext()) {
            File f = fileIterator.next();
            if (f.isFile()) {
                fileCount++;
            }
        }
        directoryStream.close();
        assertThat(fileCount, is(expectedJavaFileCount));
    }
}
