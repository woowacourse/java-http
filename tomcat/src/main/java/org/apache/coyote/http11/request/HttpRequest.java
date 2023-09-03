package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

	private final HttpRequestLine requestLine;
	private final HttpRequestHeader header;
	private final HttpRequestBody body;

	public HttpRequest(final HttpRequestLine requestLine, final HttpRequestHeader header,
		final HttpRequestBody body) {
		this.requestLine = requestLine;
		this.header = header;
		this.body = body;
	}

	public boolean hasPath(final String path) {
		return requestLine.hasPath(path);
	}

	public boolean hasMethod(final HttpMethod method) {
		return requestLine.hasMethod(method);
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String findBodyField(final String key) {
		return body.findField(key);
	}

	public String findQueryParam(final String key) {
		return requestLine.findQueryParam(key);
	}
}
