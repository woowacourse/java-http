package com.techcourse.web;

public enum HttpStatusCode {

	// success
	OK(200, "OK"),

	// client error
	BAD_REQUEST(400, "Bad Request"),
	NOT_FOUND(404, "Not Found"),

	// server error,
	INTERNAL_SERVER_ERROR(500, "Internal Server Error");

	private final int code;
	private final String message;

	HttpStatusCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
