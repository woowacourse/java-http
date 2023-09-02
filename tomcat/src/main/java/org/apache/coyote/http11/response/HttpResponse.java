package org.apache.coyote.http11.response;

public class HttpResponse {

	private final HttpStatusCode statusCode;
	private final String body;

	public HttpResponse(final HttpStatusCode statusCode, final String body) {
		this.statusCode = statusCode;
		this.body = body;
	}

	public String buildResponse() {
		return String.join(System.lineSeparator(),
			"HTTP/1.1 " + statusCode.buildResponse(),
			body
		);
	}

	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

	public String getBody() {
		return body;
	}
}
