package jacle.common.io.nio;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Accumulates a list of {@link File} objects. For use with tools like
 * {@link RecordingFileVisitor}.
 * 
 * @author rkenney
 */
public class ListFileVisitRecorder implements PathVisitRecorder {
	
	private final List<File> files;

	public ListFileVisitRecorder() {
		this(100);
	}

	public ListFileVisitRecorder(int initialListSize) {
		files = new ArrayList<File>(initialListSize);
	}

	@Override
	public void recordVisit(Path path) {
		files.add(path.toFile());
	}

	/**
	 * Returns the accumulated list
	 */
	public List<File> getFiles() {
		return files;
	}
}
