package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {

	private final HttpRequestLine requestLine;
	private final HttpRequestHeader header;

	public HttpRequest(final HttpRequestLine requestLine) {
		this(requestLine, HttpRequestHeader.empty());
	}

	private HttpRequest(final HttpRequestLine requestLine, final HttpRequestHeader header) {
		this.requestLine = requestLine;
		this.header = header;
	}

	public static HttpRequest from(final List<String> request) {
		if (request.isEmpty()) {
			throw new IllegalArgumentException("빈 요청입니다.");
		}
		final var requestLine = HttpRequestLine.from(request.get(0));
		
		if (request.size() == 1) {
			return new HttpRequest(requestLine);
		}
		final var header = HttpRequestHeader.from(request.subList(1, request.size()));
		return new HttpRequest(requestLine, header);
	}

	public boolean hasPath(final String path) {
		return requestLine.hasPath(path);
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String findQueryParam(final String key) {
		return requestLine.findQueryParam(key);
	}
}
