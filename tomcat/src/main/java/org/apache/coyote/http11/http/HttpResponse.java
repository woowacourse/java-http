package org.apache.coyote.http11.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

	private final StatusCode statusCode;
	private final String responseBody;
	private final HttpHeaders httpHeaders;

	private HttpResponse(StatusCode statusCode, String responseBody, Map<HttpHeader, String> headers) {
		this.statusCode = statusCode;
		this.responseBody = responseBody;
		this.httpHeaders = new HttpHeaders(headers);
	}

	public byte[] getBytes() {
		return getFullMessage().getBytes();
	}

	private String getFullMessage() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("HTTP/1.1 ")
			.append(statusCode.getValue())
			.append(statusCode.getMessage())
			.append("\r\n");

		for (HttpHeader key : httpHeaders.getHeaders()) {
			stringBuilder.append(key.getValue())
				.append(": ")
				.append(httpHeaders.getValue(key))
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
		return new HttpResponseBuilder()
			.statusCode(StatusCode.OK);
	}

	public static HttpResponseBuilder FOUND() {
		return new HttpResponseBuilder()
			.statusCode(StatusCode.FOUND);
	}

	public static class HttpResponseBuilder {
		private StatusCode statusCode;
		private String responseBody;
		private final Map<HttpHeader, String> headers = new LinkedHashMap<>();

		private HttpResponseBuilder() {
		}

		public HttpResponseBuilder statusCode(StatusCode statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public HttpResponseBuilder responseBody(String responseBody) {
			this.responseBody = responseBody;
			this.setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
			return this;
		}

		public HttpResponseBuilder setHeader(HttpHeader httpHeader, String value) {
			this.headers.put(httpHeader, value);
			return this;
		}

		public HttpResponse build() {
			return new HttpResponse(statusCode, responseBody, headers);
		}
	}

	@Override
	public String toString() {
		return "===HttpResponse===" + "\r\n" +
			"statusCode=" + statusCode + "\r\n" +
			"headers=" + httpHeaders + "\r\n" +
			'}';
	}
}
