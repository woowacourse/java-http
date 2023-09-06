package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpVersion {

	HTTP_0_9("HTTP/0.9"),
	HTTP_1_0("HTTP/1.0"),
	HTTP_1_1("HTTP/1.1"),
	HTTP_2_0("HTTP/2.0"),
	HTTP_3_0("HTTP/3.0");

	private final String content;

	HttpVersion(final String content) {
		this.content = content;
	}

	public static HttpVersion from(final String value) {
		return Arrays.stream(values())
			.filter(version -> version.content.equals(value.toUpperCase()))
			.findAny()
			.orElseThrow(NoSuchElementException::new);
	}

	public String getContent() {
		return content;
	}
}
