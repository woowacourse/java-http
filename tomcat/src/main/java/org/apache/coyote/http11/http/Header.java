package org.apache.coyote.http11.http;

public enum Header {

	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type"),
	LOCATION("Location")

	;

	private final String name;

	Header(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
