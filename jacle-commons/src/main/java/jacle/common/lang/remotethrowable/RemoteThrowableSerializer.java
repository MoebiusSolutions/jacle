package jacle.common.lang.remotethrowable;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * (De)serializes throwables in a way that they can be transmitted between
 * machines. Throwables processed through serialization/deserialization are
 * identified as "remote throwards", suggesting that they've been transmitted
 * from a remote system. This is helpful when reading the cause hierarchy of an
 * exception, where the local/remote split is important to identify.
 * 
 * @author rkenney
 */
public class RemoteThrowableSerializer {

	/**
	 * Creates an instance that generates JSON with whitespace for readibility
	 * 
	 * @return The instance
	 */
	public static RemoteThrowableSerializer createPretty() {
		return new RemoteThrowableSerializer(
				new GsonBuilder().setPrettyPrinting().create());
	}

	private Gson gson = new Gson();

	public RemoteThrowableSerializer() {
		this(new Gson());
	}

	public RemoteThrowableSerializer(Gson gson) {
		this.gson = gson;
	}

	/**
	 * Serializes the throwable into a form that can be deserialized by
	 * {@link #fromJson(String)}
	 * 
	 * @param t
	 *            The {@link Throwable} to be serialized
	 * 
	 * @return json result
	 */
	public String toJson(Throwable t) {
		ArrayList<ThrowablePojo> nestedThrowables = new ArrayList<ThrowablePojo>();
		Throwable cause = t;
		while (cause != null) {
			ThrowablePojo tj = new ThrowablePojo();
			tj.type = cause.getClass().getName();
			tj.message = cause.getMessage();
			tj.stack = cause.getStackTrace();
			nestedThrowables.add(tj);
			cause = cause.getCause();
		}
		return gson.toJson(nestedThrowables);
	}

	/**
	 * Deserializes a throwable that was serialized by
	 * {@link #toJson(Throwable)}
	 * 
	 * @param json
	 *            Serialized {@link Throwable}
	 * 
	 * @return The deserialized {@link Throwable}
	 */
	public Throwable fromJson(String json) {
		ThrowablePojo[] nestedThrowables = gson.fromJson(json, ThrowablePojo[].class);
		Throwable cause = null;
		for (int i=nestedThrowables.length-1; i>=0; i--) {
			cause = new RemoteThrowable(nestedThrowables[i], cause);
		}
		return cause;
	}
}
