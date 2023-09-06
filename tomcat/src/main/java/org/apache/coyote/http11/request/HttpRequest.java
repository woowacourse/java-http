package org.apache.coyote.http11.request;

import java.util.Optional;

import org.apache.coyote.http11.headers.HttpHeaderType;
import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpRequest {

	private final RequestLine requestLine;
	private final HttpHeaders headers;
	private final String body;

	private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final String body) {
		this.requestLine = requestLine;
		this.headers = headers;
		this.body = body;
	}

	public boolean equalPath(final String endPoint) {
		return requestLine.equalPath(endPoint);
	}

	public QueryParam getQueryParam() {
		return requestLine.getQueryParam();
	}

	public HttpMethod getHttpMethod() {
		return requestLine.getHttpMethod();
	}

	public HttpHeaders getHeaders() {
		return headers;
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

	public String getPath() {
		return requestLine.getPath();
	}

	public static class HttpRequestBuilder {

		private final RequestLine requestLine;
		private final HttpHeaders headers;
		private String body;

		public HttpRequestBuilder(final RequestLine requestLine, final HttpHeaders headers) {
			this.requestLine = requestLine;
			this.headers = headers;
		}

		public static HttpRequestBuilder from(final String requestHeader) {
			final String[] splitRequest = requestHeader.split("\r\n", 2);
			final RequestLine requestLine = RequestLine.from(splitRequest[0]);
			final HttpHeaders httpHeaders = HttpHeaders.from(splitRequest[1]);
			return new HttpRequestBuilder(requestLine, httpHeaders);
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
				requestLine, headers, body
			);
		}
	}
}
