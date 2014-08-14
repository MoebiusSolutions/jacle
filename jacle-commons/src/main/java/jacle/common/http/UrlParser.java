package jacle.common.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlParser {

	private static final Pattern URL_PATTERN = Pattern.compile("(https?://[^/]+/[^/]*)(/[^/]*)*");
	private String url;
	private Matcher matcher;

	public static class InvalidUrlException extends RuntimeException  {
		private static final long serialVersionUID = 1L;
		public InvalidUrlException(String message) {
			super(message);
		}
	}

	public UrlParser(String url) {
		this.url = url;
		this.matcher = URL_PATTERN.matcher(this.url);
		if (!this.matcher.matches()) {
			throw new InvalidUrlException("Invalid URL pattern: "+this.url);
		}
		
	}
	
	public String getAppUrl() {
		return matcher.group(1);
	}
}
