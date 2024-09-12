package org.apache.coyote.http11.http.request;

import java.util.List;

public class HttpRequest {

	private final HttpRequestLine requestLine;
	private final HttpRequestHeader headers;
	private final String requestBody;

	public HttpRequest(String requestLine, List<String> headers, String requestBody) {
		this.requestLine = HttpRequestLine.from(requestLine);
		this.headers = HttpRequestHeader.from(headers);
		this.requestBody = requestBody;
	}

	public HttpRequestLine getRequestLine() {
		return requestLine;
	}

	public HttpRequestHeader getHeaders() {
		return headers;
	}

	public String getRequestBody() {
		return requestBody;
	}
}
