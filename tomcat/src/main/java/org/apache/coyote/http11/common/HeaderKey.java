package org.apache.coyote.http11.common;

public enum HeaderKey {
	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type")
	;

	private final String value;

	HeaderKey(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
