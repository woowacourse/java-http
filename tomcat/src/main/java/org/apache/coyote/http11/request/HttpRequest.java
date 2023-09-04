package org.apache.coyote.http11.request;

import java.util.Optional;

import org.apache.coyote.http11.headers.HttpHeaderType;
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

	public boolean isExistJSessionId() {
		return headers.isExistJSessionId();
	}

	public Optional<String> getJSessionId() {
		return headers.findJSessionId();
	}

	public static class HttpRequestBuilder {
		private final HttpMethod httpMethod;
		private final String endPoint;
		private final QueryParam queryParam;
		private final HttpHeaders headers;
		private String body;

		private HttpRequestBuilder(HttpMethod httpMethod, String endPoint, QueryParam queryParam, HttpHeaders headers) {
			this.httpMethod = httpMethod;
			this.endPoint = endPoint;
			this.queryParam = queryParam;
			this.headers = headers;
		}

		public static HttpRequestBuilder from(final String requestHeader) {
			final String[] requestFirstLine = requestHeader.split(" ", 3);

			final HttpMethod httpMethod = HttpMethod.valueOf(requestFirstLine[0].toUpperCase());
			final String uri = requestFirstLine[1];
			final QueryParam queryParam = QueryParam.from(uri);
			final String endPoint = extractEndPoint(uri);
			final HttpHeaders httpHeaders = HttpHeaders.from(requestHeader);
			return new HttpRequestBuilder(httpMethod, endPoint, queryParam, httpHeaders);
		}

		public HttpRequestBuilder body(final String body) {
			this.body = body;
			return this;
		}

		public Integer bodyLength() {
			return headers.get(HttpHeaderType.CONTENT_LENGTH.getValue())
				.map(Integer::parseInt)
				.orElse(0);
		}

		public HttpRequest build() {
			return new HttpRequest(
				httpMethod, endPoint, queryParam, headers, body
			);
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
	}
}
