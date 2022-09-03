package org.apache.coyote.http11.response;

public enum StatusCode {

	OK(200, "OK")
	;

	private final int code;
	private final String name;

	StatusCode(final int code, final String name) {
		this.code = code;
		this.name = name;
	}

	public String toMessage() {
		return String.join(" ", String.valueOf(code), name);
	}
}
