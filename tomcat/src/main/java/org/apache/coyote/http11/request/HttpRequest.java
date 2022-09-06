package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import nextstep.jwp.exception.ExceptionType;
import nextstep.jwp.exception.InvalidHttpRequestException;

public class HttpRequest {

	private final RequestLine requestLine;
	private final RequestHeaders requestHeaders;
	private final RequestBody requestBody;

	private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
		this.requestLine = requestLine;
		this.requestHeaders = requestHeaders;
		this.requestBody = requestBody;
	}

	public static HttpRequest from(BufferedReader reader) {
		try {
			final RequestLine requestLine = RequestLine.from(reader.readLine());
			final RequestHeaders requestHeaders = RequestHeaders.from(reader);
			return new HttpRequest(requestLine, requestHeaders, new RequestBody());
		} catch (IOException e) {
			throw new InvalidHttpRequestException(ExceptionType.INVALID_REQUEST_LINE_EXCEPTION);
		}
	}

	public boolean isMatching(String url) {
		return requestLine.hasUrl(url);
	}

	public String getUrl() {
		return requestLine.getUrl();
	}

	public Map<String, String> getParams() {
		return requestLine.getParams();
	}

	public RequestLine getRequestLine() {
		return requestLine;
	}

	public RequestHeaders getRequestHeaders() {
		return requestHeaders;
	}

	public RequestBody getRequestBody() {
		return requestBody;
	}
}
