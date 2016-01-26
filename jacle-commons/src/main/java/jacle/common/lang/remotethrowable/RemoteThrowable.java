package jacle.common.lang.remotethrowable;

/**
 * Represents a {@link Throwable} that was deserialized by
 * {@link RemoteThrowableSerializer}.
 * <p>
 * 
 * Because we cannot arbitrarily instantiate specific throwable types to
 * reconstruct the original exception chain, we instantiate all deserialized as
 * this type, which works well enogh as the "cause" of a new, local exception.
 * 
 * @author rkenney
 */
// Package protected (users have no need for concrete type)
class RemoteThrowable extends Throwable {

	private static final long serialVersionUID = 1L;

	public RemoteThrowable(ThrowablePojo pojo, Throwable cause) {
		super(generateMessage(pojo), cause);
		this.setStackTrace(pojo.stack);
	}

	private static String generateMessage(ThrowablePojo pojo) {
		// If the remote exception was already identified as "REMOTE", don't re-wrap it
		if (RemoteThrowable.class.getName().equals(pojo.type)) {
			return pojo.message;
		} else{
			return String.format("REMOTE [%s] %s", pojo.type, pojo.message); 
		}
	}
}
