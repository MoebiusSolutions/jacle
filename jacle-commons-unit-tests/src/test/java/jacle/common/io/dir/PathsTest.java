package jacle.common.io.dir;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;


public class PathsTest {
    
    @Test
    public void testPathsMultipleArgsOnePath() throws  Exception{
          String expected = "src/test/resources/test.txt";
          Path path = Paths.get("src/","test/","resources/","test.txt");
          assertThat(expected,is(path.toString()));
    }

    @Test
    public void testUsrLocalIsAbsoluteTest() throws Exception {
        Path path = Paths.get("/usr/local");
        assertThat(path.resolve("/usr/local"),is(path));
        assertThat(path.isAbsolute(),is(true));
        Path resolved = path.resolve("/usr/local");
        assertThat(resolved.isAbsolute(),is(true));
        assertThat(Paths.get("/usr/local").isAbsolute(),is(true));
    }
    
    @Test
    public void testPathsComparedToFileSystemPath() throws Exception {
        Path path = Paths.get("src/","test/","resources/","test.txt");
        Path fsPath = FileSystems.getDefault().getPath("src/","test/","resources/","test.txt");
        assertThat(Files.isSameFile(path,fsPath),is(true));
    }

    @Test
      public void testPathsMultipleArgsOnePathFirstAbsolute(){
        String expected = "/src/test/resources/test.txt";
        Path path = Paths.get("/src/","/test/","//resources////","/test.txt");
        assertThat(path.toString(),is(expected));
    }

    @Test
    public void testPathsMultipleArgsOnePathNotFirstAbsolute(){
        String expected = "src/test/resources/test.txt";
        Path path = Paths.get("src/","test/","/resources/","/test.txt");
        assertThat(expected,is(path.toString()));
    }

    @Test
    public void testRelativizeFromLongBasePath() {
        Path longParentPath = Paths.get("/usr/local/lib/foo");
        Path path1 = Paths.get("bar/baz");
        Path resolved = longParentPath.resolve(path1);
        Path relative = longParentPath.relativize(resolved);
        assertThat(resolved.toString(),is(normalize("/usr/local/lib/foo/bar/baz")));
        assertThat(relative.toString(),is(normalize("bar/baz")));
    }
    
    @Test
    public void testPathGetFileName(){
        Path path = Paths.get("src/test/resources/test.txt");
        assertThat("test.txt",is(path.getFileName().toString()));
    }
    
    @Test
    public void testResolveRelativePath() {
        Path basePath = Paths.get("/usr/local");
        String relativePath = "lib/hadoop";
        Path resolved = basePath.resolve(relativePath);
        assertThat(resolved.toString(),is(normalize("/usr/local/lib/hadoop")));
    }
    
    @Test
    public void testRelativizePathFromAbsolute() {
        Path basePath = Paths.get("/usr/local");
        Path relativePath = Paths.get("/lib/hadoop");
        Path resolved = basePath.relativize(relativePath);
        assertThat(resolved.toString(),is(normalize("../../lib/hadoop")));
    }
    
    @Test
    public void testRelativizePathFromParent() {
        Path p = Paths.get("/usr");
        Path p2 = Paths.get("/usr/local/lib");
        Path p3 = Paths.get("/Users");
        Path p5 = p3.resolve(p.relativize(p2));
        assertThat(p5.toString(),is(normalize("/Users/local/lib")));
    }


    @Test
    public void testResolveSibling() {
        /*
           Directory structure
               /parent 
                      child1
                      child2
         */
        Path path = Paths.get("/parent/child1");
        Path sibling = Paths.get("child2");
        Path resolved = path.resolveSibling(sibling);
        assertThat(resolved.toString(),is(normalize("/parent/child2")));
    }
    
    @Test
    public void testResolveSilblingDirAndFileName(){
        Path base = Paths.get("/parent/foo");
        Path sibling = Paths.get("bar");
        Path resolvedSibling = base.resolveSibling(sibling);
        assertThat(resolvedSibling.toString(),is(normalize("/parent/bar")));
    }

    @Test
    public void testPathHasRootElement() {
        Path path = Paths.get("/Users/bbejeck");
        Path root = path.getRoot();
        assertNotNull(root);
        assertThat(root.toString(),is(normalize("/")));
    }
    
    @Test
    public void testRelativizeWithLongPath(){
        Path base = Paths.get("/usr");
        Path foo = base.resolve("foo");
        Path bar = foo.resolve("bar");
        Path baz = bar.resolve("baz");
        assertThat(baz.toString(),is("/usr/foo/bar/baz"));
        Path relative1 = base.relativize(baz);
        assertThat(relative1.toString(),is(normalize("foo/bar/baz")));
    }

    @Test
    public void testPathHasNoRootElement() {
        Path path = Paths.get("Users/bbejeck");
        Path root = path.getRoot();
        assertNull(root);
    }

    
    @Test
    public void testResolvePathAbsolute(){
        Path path = Paths.get("/Users/bbejeck");
        Path resolved = path.resolve("/dev");
        assertThat(resolved.isAbsolute(),is(true));
        assertThat(resolved.toString(),is(normalize("/dev")));
    }
    
    @Test
    public void testConvertPathToFile(){
        File file = Paths.get("/Users","bbejeck","dev").toFile();
        assertThat(file.getAbsolutePath(),is(normalize("/Users/bbejeck/dev")));
    }

    @Test
    public void testPathIsDirectory(){
        Path path = Paths.get("src/test/resources/");
        assertThat("resources",is(path.getFileName().toString()));
    }

    /**
	 * Returns the provided path with the platform-specific slash
	 */
    private static String normalize(String path) {
    	return path.replaceAll("[\\\\/]", File.pathSeparator);
    }
}
