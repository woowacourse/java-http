package org.apache.coyote.response;

public enum StatusCode {
	OK(200, "OK"),
	FOUND(302, "Found"),
	BAD_REQUEST(400, "Bad Request", "/400.html"),
	UNAUTHORIZED(401, "Unauthorized", "/401.html"),
	NOT_FOUND(404, "Not Found", "/404.html"),
	;

	private final int code;
	private final String message;
	private final String resourcePath;

	StatusCode(final int code, final String message) {
		this(code, message, "");
	}

	StatusCode(final int code, final String message, final String resourcePath) {
		this.code = code;
		this.message = message;
		this.resourcePath = resourcePath;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getResourcePath() {
		return resourcePath;
	}
}
