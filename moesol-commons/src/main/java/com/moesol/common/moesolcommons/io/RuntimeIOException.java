package com.moesol.common.moesolcommons.io;


import java.io.IOException;

import com.moesol.common.moesolcommons.lang.ExceptionExt;

/**
 * A runtime variant of {@link IOException}
 * 
 * @author rkenney
 */
public class RuntimeIOException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuntimeIOException() {
		super();
	}

	public RuntimeIOException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RuntimeIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuntimeIOException(String message) {
		super(message);
	}

	public RuntimeIOException(Throwable cause) {
		super(cause);
	}

	/**
	 * Returns the first {@link IOException} found in the "caused by"
	 * hierarchy. If not found, returns null.
	 */
	public IOException getIOException() {
		return ExceptionExt.I.getCauseOfType(this, IOException.class);
	}
}
