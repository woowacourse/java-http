package org.apache.coyote.http11.request;

import java.util.List;

import org.apache.coyote.http11.HttpHeaders;

public class HttpRequest {

	private final HttpHeaders headers;
	private final List<String> body;

	private HttpRequest(final HttpHeaders headers, final List<String> body) {
		this.headers = headers;
		this.body = body;
	}

	public static HttpRequest of(final List<String> headers, final List<String> body) {
		return new HttpRequest(HttpHeaders.from(headers), body);
	}
}
