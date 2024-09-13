package org.apache.coyote.http11.response;

public enum HttpStatusCode {
	OK(200, "OK"),
	REDIRECT(302, "Found"),
	NOT_FOUND(404, "Not Found"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed")
	;

	private final int statusCode;
	private final String statusMessage;

	HttpStatusCode(int statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}
}
