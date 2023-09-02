package org.apache.coyote.http11.response;

public enum HttpStatusCode {

	OK_200(200, "OK"),
	INTERNAL_SERVER_ERROR_500(500, "Internal Server Error"),
	NOT_FOUND_404(404, "Not Found");

	private final int code;
	private final String description;

	HttpStatusCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String buildResponse() {
		return code + " " + description + " ";
	}
}
