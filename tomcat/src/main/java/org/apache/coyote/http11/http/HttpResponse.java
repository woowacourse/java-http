package org.apache.coyote.http11.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

	private final StatusCode statusCode;
	private final String responseBody;

	private final Map<Header, String> headers;

	private HttpResponse(StatusCode statusCode, String responseBody, Map<Header, String> headers) {
		this.statusCode = statusCode;
		this.responseBody = responseBody;
		this.headers = headers;
	}

	public byte[] getBytes() {
		return getFullMessage().getBytes();
	}

	public String getFullMessage() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("HTTP/1.1 ")
			.append(statusCode.getCode())
			.append(statusCode.getMessage())
			.append("\r\n");

		for (Header key : headers.keySet()) {
			stringBuilder.append(key.getName())
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
		private final Map<Header, String> headers = new LinkedHashMap<>();

		private HttpResponseBuilder() {
		}

		public HttpResponseBuilder statusCode(StatusCode statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public HttpResponseBuilder responseBody(String responseBody) {
			this.responseBody = responseBody;
			this.setHeader(Header.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
			return this;
		}

		public HttpResponseBuilder setHeader(Header header, String value) {
			this.headers.put(header, value);
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
			"headers=" + headers + "\r\n" +
			'}';
	}
}
