package org.apache.coyote.http11.common;

public enum HeaderKey {
	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type"),
	COOKIE("Cookie"),
	SET_COOKIE("Set-Cookie"),
	LOCATION("Location"),
	HOST("Host"),
	CONNECTION("Connection");

	private final String value;

	HeaderKey(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
