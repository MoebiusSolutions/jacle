package jacle.common.lang.remotethrowable;

import com.google.gson.Gson;

/**
 * Represents a {@link Throwable} as a bean that can be readily serialized by
 * {@link Gson}
 * 
 * @author rkenney
 */
// Package protected
class ThrowablePojo {
	public String type;
	public String message;
	public StackTraceElement[] stack;
}
