package org.apache.coyote.http11.request;

import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpRequest {

	private final HttpMethod httpMethod;
	private final String endPoint;
	private final HttpHeaders headers;

	private HttpRequest(final HttpMethod httpMethod, final String endPoint, final HttpHeaders headers) {
		this.httpMethod = httpMethod;
		this.endPoint = endPoint;
		this.headers = headers;
	}

	public static HttpRequest from(final String request) {
		final String[] requestFirstLine = request.split(" ", 3);
		final HttpMethod httpMethod = HttpMethod.valueOf(requestFirstLine[0].toUpperCase());
		final String endPoint = requestFirstLine[1];
		final HttpHeaders headers = HttpHeaders.from(request);
		return new HttpRequest(httpMethod, endPoint, headers);
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}
}
