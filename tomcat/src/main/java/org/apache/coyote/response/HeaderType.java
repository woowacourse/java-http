package org.apache.coyote.response;

public enum HeaderType {
	CONTENT_TYPE("Content-Type"),
	CONTENT_LENGTH("Content-Length"),
	LOCATION("Location"),
	SET_COOKIE("Set-Cookie"),
	COOKIE("Cookie");

	private final String name;

	HeaderType(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
