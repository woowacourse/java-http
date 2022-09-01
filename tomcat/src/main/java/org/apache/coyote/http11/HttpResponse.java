package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

	private final StatusCode statusCode;
	private final String responseBody;

	private final Map<String, String> headers;

	private HttpResponse(StatusCode statusCode, String responseBody, Map<String, String> headers) {
		this.statusCode = statusCode;
		this.responseBody = responseBody;
		this.headers = headers;
	}

	public String getFullMessage() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("HTTP/1.1 ")
			.append(statusCode.getCode())
			.append(statusCode.getMessage())
			.append("\r\n");

		for (String key : headers.keySet()) {
			stringBuilder.append(key)
				.append(": ")
				.append(headers.get(key))
				.append(" ")
				.append("\r\n");
		}

		if (responseBody != null) {
			stringBuilder.append("\r\n");
			stringBuilder.append(responseBody);
		}

		return stringBuilder.toString();
	}

	public static HttpResponseBuilder builder() {
		return new HttpResponseBuilder();
	}

	public static HttpResponseBuilder OK() {
		return new HttpResponseBuilder(StatusCode.OK);
	}

	public static HttpResponseBuilder FOUND() {
		return new HttpResponseBuilder(StatusCode.FOUND);
	}

	public static class HttpResponseBuilder {
		private StatusCode statusCode;
		private String responseBody;
		private final Map<String, String> headers = new HashMap<>();

		private HttpResponseBuilder() {
		}

		private HttpResponseBuilder(StatusCode statusCode) {
			this.statusCode = statusCode;
		}

		public HttpResponseBuilder statusCode(StatusCode statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public HttpResponseBuilder responseBody(String responseBody) {
			this.responseBody = responseBody;
			this.setHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
			return this;
		}

		public HttpResponseBuilder setHeader(String key, String value) {
			this.headers.put(key, value);
			return this;
		}

		public HttpResponse build() {
			return new HttpResponse(statusCode, responseBody, headers);
		}
	}
}
