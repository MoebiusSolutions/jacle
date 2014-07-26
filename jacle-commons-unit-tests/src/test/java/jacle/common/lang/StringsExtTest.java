package jacle.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jacle.common.io.CloseablesExt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

public class StringsExtTest {

	@Test
	public void testToStream() throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			StringsExt.I.toStream("mock value", stream, StandardCharsets.UTF_8);
			assertTrue(Arrays.equals(new byte[]{'m', 'o', 'c', 'k', ' ', 'v', 'a', 'l', 'u', 'e'}, stream.toByteArray()));
		} finally {
			CloseablesExt.closeQuietly(stream);
		}
	}

	@Test
	public void testFromStream() throws Exception {
		ByteArrayInputStream stream = new ByteArrayInputStream(new byte[]{'m', 'o', 'c', 'k', ' ', 'v', 'a', 'l', 'u', 'e'});
		try {
			String string = StringsExt.I.fromStream(stream, StandardCharsets.UTF_8);
			assertEquals("mock value", string);
		} finally {
			CloseablesExt.closeQuietly(stream);
		}
	}
}
