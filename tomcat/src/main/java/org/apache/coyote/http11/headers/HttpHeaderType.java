package org.apache.coyote.http11.headers;

public enum HttpHeaderType {
	ACCEPT("Accept"),
	CONTENT_TYPE("Content-Type"),
	CONTENT_LENGTH("Content-Length"),
	LOCATION("Location");

	private final String value;

	HttpHeaderType(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
