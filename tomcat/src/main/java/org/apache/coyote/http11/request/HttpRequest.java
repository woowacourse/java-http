package org.apache.coyote.http11.request;

import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpRequest {

	private final HttpMethod httpMethod;
	private final String endPoint;
	private final QueryParam queryParam;
	private final HttpHeaders headers;

	private HttpRequest(final HttpMethod httpMethod, final String endPoint, final QueryParam queryParam,
		final HttpHeaders headers) {
		this.httpMethod = httpMethod;
		this.endPoint = endPoint;
		this.queryParam = queryParam;
		this.headers = headers;
	}

	public static HttpRequest from(final String request) {
		final String[] requestFirstLine = request.split(" ", 3);

		final HttpMethod httpMethod = HttpMethod.valueOf(requestFirstLine[0].toUpperCase());
		final String uri = requestFirstLine[1];
		final String endPoint = extractEndPoint(uri);
		final QueryParam queryParam = QueryParam.from(uri);
		final HttpHeaders headers = HttpHeaders.from(request);

		return new HttpRequest(httpMethod, endPoint, queryParam, headers);
	}

	private static String extractEndPoint(final String uri) {
		final int index = uri.indexOf("?");
		if (index == -1) {
			return uri;
		}
		return uri.substring(0, index);
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

	public QueryParam getQueryParam() {
		return queryParam;
	}
}
