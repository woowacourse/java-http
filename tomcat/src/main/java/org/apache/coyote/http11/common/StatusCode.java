package org.apache.coyote.http11.common;

public enum StatusCode {

	OK(200, "OK"),
	FOUND(302, "Found"),
	NOT_FOUND(404, "Not Found");

	private final int code;
	private final String text;

	StatusCode(int code, String text) {
		this.code = code;
		this.text = text;
	}

	public String toResponseString() {
		return code + " " + text;
	}
}

