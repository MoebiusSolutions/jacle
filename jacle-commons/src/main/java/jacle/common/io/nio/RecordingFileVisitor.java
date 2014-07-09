package jacle.common.io.nio;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Walks files and directories, applying conditions such as
 * {@link #setRequireIsFile()} for filtering, and then reports the resulting
 * files to a {@link FileVisitRecorder}.
 * 
 * @author rkenney
 */
public class RecordingFileVisitor extends SimpleFileVisitor<Path> {

	private PathVisitRecorder pathRecorder;
	private boolean requiresIsFile;

	public RecordingFileVisitor(PathVisitRecorder recorder) {
		this.pathRecorder = recorder;
	}
	
    /**
     * If called, restricts the collected entries to files (verus files and directories)
     */
	public RecordingFileVisitor setRequireIsFile() {
		requiresIsFile = true;
		return this;
	}
    
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException
    {
    	FileVisitResult result = super.visitFile(path, attrs);
    	if (result != FileVisitResult.CONTINUE) {
    		return result;
    	}
    	File file = path.toFile();
    	if (requiresIsFile && !file.isFile()) { 
    		return FileVisitResult.CONTINUE;
    	}
    	pathRecorder.recordVisit(path);
        return FileVisitResult.CONTINUE;
    }
}
