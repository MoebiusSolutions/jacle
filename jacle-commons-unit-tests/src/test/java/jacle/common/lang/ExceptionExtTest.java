package jacle.common.lang;

import static org.junit.Assert.assertEquals;
import jacle.common.io.RuntimeIOException;
import jacle.common.lang.ExceptionExt;

import java.io.IOException;

import org.junit.Test;

public class ExceptionExtTest {

	@Test
	public void testGetCauseOfType() throws Exception {
		// Verify immediate cause is returned
		RuntimeIOException e = new RuntimeIOException("Mock failure",
				new IOException("First IOException",
				new IOException("Second IOException")));
		IOException cause = ExceptionExt.I.getCauseOfType(e, IOException.class);
		assertEquals("First IOException", cause.getMessage());

		// Verify transient causes are ignored
		e = new RuntimeIOException("Mock failure",
				new Exception("Transient exception",
				new IOException("First IOException",
				new Exception("Transient exception",
				new IOException("Second IOException")))));
		cause = ExceptionExt.I.getCauseOfType(e, IOException.class);
		assertEquals("First IOException", cause.getMessage());

		// Verify derived types are returned
		e = new RuntimeIOException("Mock failure",
				new Exception("Transient exception",
				new MyDerivedIOException("First IOException",
				new Exception("Transient exception",
				new IOException("Second IOException")))));
		cause = ExceptionExt.I.getCauseOfType(e, IOException.class);
		assertEquals("First IOException", cause.getMessage());

		// Verify with no match, null is returned
		e = new RuntimeIOException("Mock failure",
				new Exception("Transient exception",
				new RuntimeException("Transient exception",
				new Exception("Transient exception",
				new RuntimeException("Transient exception")))));
		assertEquals(null, ExceptionExt.I.getCauseOfType(e, IOException.class));
	}
	
	private static class MyDerivedIOException extends IOException {
		private static final long serialVersionUID = 1L;
		public MyDerivedIOException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
