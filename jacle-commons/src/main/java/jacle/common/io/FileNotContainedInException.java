package jacle.common.io;

public class FileNotContainedInException extends RuntimeIOException {

	private static final long serialVersionUID = 1L;
	
	public FileNotContainedInException() {
		super();
	}

	public FileNotContainedInException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNotContainedInException(String message) {
		super(message);
	}

	public FileNotContainedInException(Throwable cause) {
		super(cause);
	}
}
