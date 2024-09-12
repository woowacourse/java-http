package org.apache.coyote.http11.http.response;

public class HttpResponseStartLine {

	private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

	private String httpVersion;
	private HttpStatusCode statusCode;

	public HttpResponseStartLine() {
		this.httpVersion = DEFAULT_HTTP_VERSION;
	}

	public String toResponseMessage() {
		return httpVersion + " " + statusCode.getCode() + " " + statusCode.getMessage() + " ";
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.statusCode = statusCode;
	}
}
