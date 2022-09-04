package org.apache.coyote.http11.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

	private final HttpStatus httpStatus;
	private final String responseBody;
	private final HttpHeaders httpHeaders;

	private HttpResponse(HttpStatus httpStatus, String responseBody, Map<HttpHeader, String> headers) {
		this.httpStatus = httpStatus;
		this.responseBody = responseBody;
		this.httpHeaders = new HttpHeaders(headers);
	}

	public byte[] getBytes() {
		return getFullMessage().getBytes();
	}

	private String getFullMessage() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("HTTP/1.1 ")
			.append(httpStatus.value())
			.append(httpStatus.getMessage())
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
			.statusCode(HttpStatus.OK);
	}

	public static HttpResponseBuilder FOUND() {
		return new HttpResponseBuilder()
			.statusCode(HttpStatus.FOUND);
	}

	public static class HttpResponseBuilder {
		private HttpStatus httpStatus;
		private String responseBody;
		private final Map<HttpHeader, String> headers = new LinkedHashMap<>();

		private HttpResponseBuilder() {
		}

		public HttpResponseBuilder statusCode(HttpStatus httpStatus) {
			this.httpStatus = httpStatus;
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
			return new HttpResponse(httpStatus, responseBody, headers);
		}
	}

	@Override
	public String toString() {
		return "===HttpResponse===" + "\r\n" +
			"statusCode=" + httpStatus + "\r\n" +
			"headers=" + httpHeaders + "\r\n" +
			'}';
	}
}
