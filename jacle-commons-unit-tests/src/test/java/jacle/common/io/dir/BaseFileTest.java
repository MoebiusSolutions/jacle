package jacle.common.io.dir;

import jacle.common.io.FilesExt;
import jacle.commontest.JUnitFiles;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;

public class BaseFileTest {

	private static final JUnitFiles files = new JUnitFiles();
	protected String baseDir = files.getFile("test-files").getPath();
    protected String dir1 = "dir1";
    protected String dir2 = "dir2";
    protected String fileName;
    protected String fooDir = "foo";
    protected String copyDir = "copy";
    protected String tempDir = "temp";
    protected Path basePath;
    protected Path dir1Path;
    protected Path dir2Path;
    protected Path fooPath;
    protected Path sourcePath;
    protected Path copyPath;
    protected String[] fakeJavaFiles = new String[]{"Code.java", "Foo.java", "Bar.java", "Baz.java"};
    protected String[] textFileNames = new String[]{"persons.csv", "counts.csv", "CountyTaxes.csv"};


    @Before
    public void setUp() throws Exception {
    	files.before();
        createPaths();
        createDirectories();
        generateFile(Paths.get(baseDir, fileName), 500);
        generateFiles(basePath, fakeJavaFiles, textFileNames);
        generateFiles(dir1Path, fakeJavaFiles);
        generateFiles(dir2Path, fakeJavaFiles);
    }

    protected void createPaths() {
        basePath = Paths.get(baseDir);
        dir1Path = basePath.resolve(dir1);
        dir2Path = basePath.resolve(dir2);
        fileName = "test.txt";
        sourcePath = basePath.resolve(fileName);
        copyPath = basePath.resolve(copyDir);
        fooPath = basePath.resolve(fooDir);
    }

    protected void createDirectories() throws Exception {
        Files.createDirectories(dir1Path);
        Files.createDirectories(dir2Path);
        Files.createDirectories(copyPath);
        Files.createDirectories(fooPath);
    }

    private void generateFile(Path path, int numberLines) throws Exception {
    	FilesExt.delete(path.toFile());
    	for (int i=0; i<numberLines; i++) {
        	FilesExt.append("xxx\n", path.toFile(), StandardCharsets.UTF_8);
    	}
    }

    protected void generateFiles(Path path, String[]... fileNames) throws Exception {
        for (String[] fileNamesArray : fileNames) {
            for (String fileName : fileNamesArray) {
                generateFile(path.resolve(fileName), 10);
            }
        }
    }
}
