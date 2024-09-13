package org.apache.coyote.http11.common;

public enum HTTP_DELIMITER {
	REQUEST_LINE_DELIMITER(" "),
	BODY_PROPERTY_DELIMITER("&"),
	BODY_KEY_VALUE_DELIMITER("=")
	;

	private final String value;

	HTTP_DELIMITER(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
