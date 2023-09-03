package org.apache.coyote.http11.response;

public enum ResponseHeaderType {
	CONTENT_TYPE("Content-Type"),
	CONTENT_LENGTH("Content-Length"),
	LOCATION("Location"),
	;

	private final String name;

	ResponseHeaderType(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
