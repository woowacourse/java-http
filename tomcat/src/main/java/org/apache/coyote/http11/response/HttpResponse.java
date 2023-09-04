package org.apache.coyote.http11.response;

import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpResponse {

	private final HttpStatusCode statusCode;
	private final String body;
	private final HttpHeaders headers;

	public HttpResponse(final HttpStatusCode statusCode, final String body, final HttpHeaders headers) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = headers;
	}

	public String buildResponse() {
		return String.join("\r\n",
			"HTTP/1.1 " + statusCode.buildResponse(),
			headers.build(),
			body
		);
	}
}
