package org.apache.coyote.http11.request;

import java.util.Optional;

import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpRequest {

	private static final String BODY_DELIMITER = System.lineSeparator() + System.lineSeparator();

	private final HttpMethod httpMethod;
	private final String endPoint;
	private final QueryParam queryParam;
	private final HttpHeaders headers;
	private final String body;

	private HttpRequest(final HttpMethod httpMethod, final String endPoint, final QueryParam queryParam,
		final HttpHeaders headers, final String body) {
		this.httpMethod = httpMethod;
		this.endPoint = endPoint;
		this.queryParam = queryParam;
		this.headers = headers;
		this.body = body;
	}

	public static HttpRequest from(final String request) {
		final String[] requestFirstLine = request.split(" ", 3);

		final HttpMethod httpMethod = HttpMethod.valueOf(requestFirstLine[0].toUpperCase());
		final String uri = requestFirstLine[1];
		final QueryParam queryParam = QueryParam.from(uri);
		final String endPoint = extractEndPoint(uri);

		//추후 리팩터링 하기
		final String[] requestSplitByBodyDelimiter = request.split(BODY_DELIMITER, 2);
		final HttpHeaders headers = HttpHeaders.from(requestSplitByBodyDelimiter[0]);
		final String body = extractBody(requestSplitByBodyDelimiter);

		return new HttpRequest(httpMethod, endPoint, queryParam, headers, body);
	}

	private static String extractBody(final String[] requestSplitByBodyDelimiter) {
		if (requestSplitByBodyDelimiter.length == 1) {
			return null;
		}
		return requestSplitByBodyDelimiter[1];
	}

	private static String extractEndPoint(final String uri) {
		//TODO: 추후 리팩터링 시 endPoint와 queryParam 묶는 클래스 생성
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

	public Optional<String> getBody() {
		return Optional.ofNullable(body);
	}
}
