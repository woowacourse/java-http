package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

	private HttpStatus httpStatus;
	private String responseBody;
	private final HttpHeaders httpHeaders;

	public HttpResponse() {
		this(null, null, new HashMap<>());
	}

	public HttpResponse(HttpStatus httpStatus, String responseBody, Map<HttpHeader, String> headers) {
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

	public void setStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public void setBody(String body) {
		this.responseBody = body;
		this.addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
	}

	public void addHeader(HttpHeader httpHeader, String value) {
		this.httpHeaders.addHeader(httpHeader, value);
	}

	@Override
	public String toString() {
		return "===HttpResponse===" + "\r\n" +
			"statusCode=" + httpStatus + "\r\n" +
			"headers=" + httpHeaders + "\r\n" +
			'}';
	}
}
