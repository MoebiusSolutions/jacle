package jacle.common.io.nio;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Accumulates a list of file path strings. For use with tools like
 * {@link RecordingFileVisitor}.
 * 
 * @author rkenney
 */
public class ListFilePathVisitRecorder implements PathVisitRecorder {
	
	private final List<String> paths;

	public ListFilePathVisitRecorder() {
		this(100);
	}

	public ListFilePathVisitRecorder(int initialListSize) {
		paths = new ArrayList<String>(initialListSize);
	}

	@Override
	public void recordVisit(Path path) {
		paths.add(path.toString());
	}

	/**
	 * Returns the accumulated list
	 */
	public List<String> getPaths() {
		return paths;
	}
}
