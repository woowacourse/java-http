package org.apache.coyote.http11.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public record Body(String value) {
	private static final String CONTENT_LENGTH = "Content-Length";

	public static Body request(Headers headers, BufferedReader reader) throws IOException {
		return new Body(parseBody(headers, reader));
	}

	private static String parseBody(Headers headers, BufferedReader reader) throws IOException {
		int contentLength = Integer.parseInt(headers.getValue(CONTENT_LENGTH));
		if (contentLength > 0) {
			char[] body = new char[contentLength];
			reader.read(body, 0, contentLength);
			return new String(body);
		}
		return null;
	}

	public int getContentLength() {
		return this.value.getBytes().length;
	}

	public Properties parseProperty() {
		Properties properties = new Properties();
		Arrays.asList(value.split("&"))
			.forEach(properties::add);
		return properties;
	}
}
