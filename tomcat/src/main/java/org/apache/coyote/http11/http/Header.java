package org.apache.coyote.http11.http;

public enum Header {

	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type"),
	LOCATION("Location")

	;

	private final String value;

	Header(String name) {
		this.value = name;
	}

	public String getValue() {
		return value;
	}
}
