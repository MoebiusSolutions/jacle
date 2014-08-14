package jacle.common.http;

import static org.junit.Assert.assertEquals;
import jacle.common.http.UrlParser.InvalidUrlException;
import junit.framework.Assert;

import org.junit.Test;

public class UrlParserTest {

	@Test
	public void testGetAppUrl() throws Exception {
		UrlParser parser = new UrlParser("http://my-server:8080/my-app/my/sub/path?with=options");
		assertEquals("http://my-server:8080/my-app", parser.getAppUrl());
	}

	@Test
	public void testGetAppUrl_InvalidUrl() throws Exception {
		try {
			new UrlParser("BLAH://my-server:8080/my-app/my/sub/path?with=options");
			Assert.fail("Expected exception");
		} catch (InvalidUrlException e) {
			// Succes
		}
	}
}
