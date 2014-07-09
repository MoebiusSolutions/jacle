package jacle.common.io.nio;

import java.nio.file.Path;

public interface PathVisitRecorder {
	public void recordVisit(Path file);
}
