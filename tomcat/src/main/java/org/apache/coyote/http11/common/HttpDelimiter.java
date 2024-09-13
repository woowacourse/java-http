package org.apache.coyote.http11.common;

public enum HttpDelimiter {
	REQUEST_LINE_DELIMITER(" "),
	BODY_PROPERTY_DELIMITER("&"),
	BODY_KEY_VALUE_DELIMITER("="),
	SESSION_DELIMITER("; "),
	HEADER_KEY_VALUE_DELIMITER(":")
	;

	private final String value;

	HttpDelimiter(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
