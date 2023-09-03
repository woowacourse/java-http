package org.apache.coyote.http11.response;

public enum HttpResponseHeaderType {
	CONTENT_TYPE("Content-Type"),
	CONTENT_LENGTH("Content-Length"),
	LOCATION("Location"),
	;

	private final String name;

	HttpResponseHeaderType(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
