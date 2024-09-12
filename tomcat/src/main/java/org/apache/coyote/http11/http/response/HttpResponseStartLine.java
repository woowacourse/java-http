package org.apache.coyote.http11.http.response;

public class HttpResponseStartLine {

	private final String httpVersion;
	private final HttpStatusCode statusCode;

	public HttpResponseStartLine(String httpVersion, HttpStatusCode statusCode) {
		this.httpVersion = httpVersion;
		this.statusCode = statusCode;
	}

	public String toResponseMessage() {
		return httpVersion + " " + statusCode.getCode() + " " + statusCode.getMessage() + " ";
	}
}
