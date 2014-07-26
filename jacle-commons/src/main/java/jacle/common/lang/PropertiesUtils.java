package jacle.common.lang;

import jacle.common.io.CloseablesExt;
import jacle.common.io.FilesExt;
import jacle.common.io.RuntimeIOException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesUtils {

	/**
	 * Static accessor
	 */
	public static final PropertiesUtils I = new PropertiesUtils();

	public String toString(Properties p) throws RuntimeIOException  {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			toStream(p, stream);
		} finally {
			CloseablesExt.closeQuietly(stream);
		}
		// This is the charset Properties.write() uses
		try {
			return stream.toString(StandardCharsets.ISO_8859_1.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeIOException("Failed to write properties to string", e);
		}
	}

	public void toFile(Properties p, File file) throws RuntimeIOException  {
		FileOutputStream stream = FilesExt.newOutputStream(file);
		try {
			toStream(p, stream);
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to write properties to [%s]", file), e);
		} finally {
			CloseablesExt.closeQuietly(stream);
		}
	}

	public void toStream(Properties p, OutputStream stream) throws RuntimeIOException {
		try {
			p.store(stream, "");
		} catch (Exception e) {
			throw new RuntimeIOException("Failed to write properties to stream", e);
		}
	}

	public Properties fromString(String string) throws RuntimeIOException  {
		ByteArrayInputStream stream = new ByteArrayInputStream(string.getBytes());
		try {
			return fromStream(stream);
		} catch (Exception e) {
			throw new RuntimeIOException("Failed to read properties from string", e);
		} finally {
			CloseablesExt.closeQuietly(stream);
		}
	}

	public Properties fromFile(File file) throws RuntimeIOException  {
		FileInputStream stream = FilesExt.newInputStream(file);
		try {
			return fromStream(stream);
		} catch (Exception e) {
			throw new RuntimeIOException(String.format("Failed to read properties from [%s]", file), e);
		} finally {
			CloseablesExt.closeQuietly(stream);
		}
	}

	public Properties fromStream(InputStream stream) throws RuntimeIOException  {
		try {
			Properties properties = new Properties();
			properties.load(stream);
			return properties;
		} catch (Exception e) {
			throw new RuntimeIOException("Failed to read properties from stream", e);
		}
	}
}
