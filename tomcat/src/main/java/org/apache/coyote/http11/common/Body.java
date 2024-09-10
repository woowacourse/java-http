package org.apache.coyote.http11.common;

import java.io.BufferedReader;
import java.io.IOException;

public class Body {
	private static final String CONTENT_LENGTH = "Content-Length";

	private final String value;

	public Body(Headers headers, BufferedReader reader) throws IOException {
		this.value = parseBody(headers, reader);
	}

	private String parseBody(Headers headers, BufferedReader reader) throws IOException {
		int contentLength = Integer.parseInt(headers.getValue(CONTENT_LENGTH));
		if(contentLength > 0) {
			char[] body = new char[contentLength];
			reader.read(body, 0, contentLength);
			return new String(body);
		}
		return null;
	}
}
