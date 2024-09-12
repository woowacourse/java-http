package org.apache.coyote.http11.http;

public enum HttpHeader {

	LOCATION("Location"),
	COOKIE("Cookie"),
	SET_COOKIE("Set-Cookie"),
	CONTENT_TYPE("Content-Type"),
	CONTENT_LENGTH("Content-Length"),
	;

	private final String name;

	HttpHeader(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
