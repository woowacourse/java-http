package org.apache.coyote.http11.request;

import java.util.List;

import org.apache.coyote.http11.HttpHeaders;

public class HttpRequest {

	private final RequestLine requestLine;
	private final HttpHeaders headers;
	private final List<String> body;

	private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final List<String> body) {
		this.requestLine = requestLine;
		this.headers = headers;
		this.body = body;
	}

	public static HttpRequest of(final String requestLine, final List<String> headers, final List<String> body) {
		return new HttpRequest(RequestLine.from(requestLine), HttpHeaders.from(headers), body);
	}
}
