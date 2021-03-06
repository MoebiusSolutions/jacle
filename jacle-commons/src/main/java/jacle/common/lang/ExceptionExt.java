package jacle.common.lang;


import java.io.IOException;

/**
 * A runtime variant of {@link IOException}
 * 
 * @author rkenney
 */
// TODO [RK] JACLE: Remove other instances of this from otm
public class ExceptionExt {

	/**
	 * Static accessor
	 */
	public static ExceptionExt I = new ExceptionExt();
	
	/**
	 * Returns the first exception of the specified type found in the
	 * "caused by" hierarchy of the provided exception. If not found, returns
	 * null.
	 * 
	 * @param <T>
	 *            The type to search for
	 * 
	 * @param exception
	 *            The exception to search within
	 * @param targetType
	 *            The type to search for
	 *
	 * @return The matched inner exception
	 */
	public <T> T getCauseOfType(Throwable exception, Class<? extends T> targetType) {
		Throwable cause = exception.getCause();
		while (cause != null) {
			if (targetType.isInstance(cause)) {
				@SuppressWarnings("unchecked")
				T t = (T) cause;
				return t;
			}
			cause = cause.getCause();
		}
		return null;
	}
}
