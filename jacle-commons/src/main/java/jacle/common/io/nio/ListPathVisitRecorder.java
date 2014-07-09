package jacle.common.io.nio;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Accumulates a list of {@link Path} objects. For use with tools like
 * {@link RecordingFileVisitor}.
 * 
 * @author rkenney
 */
public class ListPathVisitRecorder implements PathVisitRecorder {
	
	private final List<Path> paths;

	public ListPathVisitRecorder() {
		this(100);
	}

	public ListPathVisitRecorder(int initialListSize) {
		paths = new ArrayList<Path>(initialListSize);
	}

	@Override
	public void recordVisit(Path path) {
		paths.add(path);
	}

	/**
	 * Returns the accumulated list
	 */
	public List<Path> getPaths() {
		return paths;
	}
}
