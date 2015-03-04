package jacle.common.io.dir.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jacle.common.io.dir.BaseFileTest;
import jacle.common.io.dir.events.AsynchronousRecursiveDirectoryStream;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class AsynchronousRecursiveDirectoryStreamTest extends BaseFileTest {
    private int expectedJavaFileCount;
    private int expectedTotalFileCount;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        expectedJavaFileCount = fakeJavaFiles.length * 3;
        expectedTotalFileCount = expectedJavaFileCount + textFileNames.length + 1;
    }

    @Test
    public void testRecursiveDirectoryStream() throws Exception {
        int fileCount = 0;
        try (DirectoryStream<Path> directoryStream = new AsynchronousRecursiveDirectoryStream(basePath, "*")) {
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(fileCount, is(expectedTotalFileCount));
    }

    @Test
    public void testRecursiveDirectoryStreamJavaFiles() throws Exception {
        int fileCount = 0;
        try (DirectoryStream<Path> directoryStream = new AsynchronousRecursiveDirectoryStream(basePath, "*.java")) {
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    fileCount++;
                }
            }
        }
        assertThat(fileCount, is(expectedJavaFileCount));
    }

    @Test (expected = IllegalStateException.class)
    public void testErrorWhenIteratorCalledAfterClose() throws Exception {
            DirectoryStream<Path> directoryStream = new AsynchronousRecursiveDirectoryStream(basePath,"*");
            directoryStream.close();
            directoryStream.iterator();
    }

    @Test (expected = IllegalStateException.class)
    public void testErrorWhenIteratorCalledTwice() throws Exception {
        DirectoryStream<Path> directoryStream = new AsynchronousRecursiveDirectoryStream(basePath,"*");
        directoryStream.iterator();
        directoryStream.close();
        directoryStream.iterator();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testErrorOnRemove() throws Exception{
        try(DirectoryStream<Path> directoryStream = new AsynchronousRecursiveDirectoryStream(basePath,"*")){
            Iterator<Path> it = directoryStream.iterator();
            while(it.hasNext()){
                it.remove();
            }
        }
    }


}
