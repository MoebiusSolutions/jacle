package jacle.common.io;

import jacle.common.lang.Ref;

import java.io.Closeable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides extensions to {@link com.google.common.io.Closeables}
 * 
 * @author rkenney
 */
public class CloseablesExt {

	 private static final Logger LOGGER = Logger.getLogger(CloseablesExt.class.getName());

	/**
	 * Closes the provided {@link Closeable}, swallowing/logging any exception
	 **/
	public static void closeQuietly(Closeable closeable) {
		if (null == closeable) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to close Closeable", e);
		}
	}

    /**
     * Closes the {@link Closeable} provided via a {@link Ref} object,
     * swallowing/logging any exception, and setting the {@link Ref} value to
     * null. The provided {@link Ref} must be non-null, but it may point to a
     * null value.
     **/
    public static void closeQuietly(Ref<? extends Closeable> refToCloseable) {
        if (null == refToCloseable.get()) {
            return;
        }
        try {
            refToCloseable.get().close();
            refToCloseable.set(null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to close Closeable", e);
        }
    }
}
