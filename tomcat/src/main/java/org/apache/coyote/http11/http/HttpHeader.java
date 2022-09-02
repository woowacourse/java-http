package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.Optional;

public enum HttpHeader {

	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type"),
	LOCATION("Location"),

	;

	private final String value;

	HttpHeader(String value) {
		this.value = value;
	}

	public static Optional<HttpHeader> from(String value) {
		return Arrays.stream(values())
			.filter(header -> header.value.equals(value))
			.findAny();
	}

	public String getValue() {
		return value;
	}
}
