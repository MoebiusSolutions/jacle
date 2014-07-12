package jacle.common.lang.remotethrowable;

import static org.junit.Assert.assertEquals;
import jacle.common.lang.JavaUtil;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class RemoteThrowableSerializerTest {

	private Exception m_exception;
	
	@Before
	public void setUp() throws Exception { 
		m_exception =
			new RuntimeException("Failed on Server A",
				new IOException("Failed to read something"));
	}
	
	@Test
	public void testRepeatedNesting() throws Exception {
		Throwable exception;
		String json;
		RemoteThrowableSerializer serializer = RemoteThrowableSerializer.createPretty();
		// Serialize
		json = serializer.toJson(m_exception);
		// Deserialize
		exception = serializer.fromJson(json);
		// Nest
		exception = new IllegalArgumentException("Failed on Server B", exception);
		// Serialize
		json = serializer.toJson(exception);
		// Deserialize
		exception = serializer.fromJson(json);
		// Nest
		exception = new Exception("Failed locally", exception);

		assertEquals("Failed locally",
				exception.getMessage());
		assertEquals("jacle.common.lang.remotethrowable.RemoteThrowableSerializerTest",
				exception.getStackTrace()[0].getClassName());
		assertEquals(JavaUtil.I.getMethodName(),
				exception.getStackTrace()[0].getMethodName());
		assertEquals("RemoteThrowableSerializerTest.java",
				exception.getStackTrace()[0].getFileName());
//		assertEquals(38,
//				exception.getStackTrace()[0].getLineNumber());

		exception = exception.getCause();
		assertEquals("REMOTE [java.lang.IllegalArgumentException] Failed on Server B",
				exception.getMessage());
		assertEquals("jacle.common.lang.remotethrowable.RemoteThrowableSerializerTest",
				exception.getStackTrace()[0].getClassName());
		assertEquals(JavaUtil.I.getMethodName(),
				exception.getStackTrace()[0].getMethodName());
		assertEquals("RemoteThrowableSerializerTest.java",
				exception.getStackTrace()[0].getFileName());
//		assertEquals(32,
//				exception.getStackTrace()[0].getLineNumber());
		
		exception = exception.getCause();
		assertEquals("REMOTE [java.lang.RuntimeException] Failed on Server A",
				exception.getMessage());
		assertEquals("jacle.common.lang.remotethrowable.RemoteThrowableSerializerTest",
				exception.getStackTrace()[0].getClassName());
		assertEquals("setUp",
				exception.getStackTrace()[0].getMethodName());
		assertEquals("RemoteThrowableSerializerTest.java",
				exception.getStackTrace()[0].getFileName());
//		assertEquals(18,
//				exception.getStackTrace()[0].getLineNumber());
		
		exception = exception.getCause();
		assertEquals("REMOTE [java.io.IOException] Failed to read something",
				exception.getMessage());
		assertEquals("jacle.common.lang.remotethrowable.RemoteThrowableSerializerTest",
				exception.getStackTrace()[0].getClassName());
		assertEquals("setUp",
				exception.getStackTrace()[0].getMethodName());
		assertEquals("RemoteThrowableSerializerTest.java",
				exception.getStackTrace()[0].getFileName());
//		assertEquals(19,
//				exception.getStackTrace()[0].getLineNumber());
		exception = exception.getCause();
		
		assertEquals(null, exception);
	}
}
