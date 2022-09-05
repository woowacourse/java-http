package org.apache.coyote.http11.request;

import java.util.Map;

import nextstep.jwp.exception.ExceptionType;
import nextstep.jwp.exception.InvalidHttpRequestException;

public class RequestLine {
	private static final int REQUEST_LINE_SIZE = 3;
	private static final String GAP = " ";
	private static final int METHOD_INDEX = 0;
	private static final int URL_INDEX = 1;
	private static final int VERSION_INDEX = 2;

	private final HttpMethod method;
	private final Url url;
	private final String version;

	private RequestLine(HttpMethod httpMethod, Url url, String version) {
		this.method = httpMethod;
		this.url = url;
		this.version = version;
	}

	public static RequestLine from(String line) {
		final String[] requestLine = line.split(GAP);
		validateLength(requestLine);
		final HttpMethod method = HttpMethod.From(requestLine[METHOD_INDEX]);
		final Url url = Url.from(requestLine[URL_INDEX]);
		final String version = requestLine[VERSION_INDEX];

		return new RequestLine(method, url, version);
	}

	private static void validateLength(String[] requestLine) {
		if (requestLine.length != REQUEST_LINE_SIZE) {
			throw new InvalidHttpRequestException(ExceptionType.INVALID_REQUEST_LINE_EXCEPTION);
		}
	}

	public boolean hasUrl(String url) {
		return this.url.getPath().equals(url);
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url.getPath();
	}

	public Map<String, String> getParams() {
		return url.getParams();
	}

	public String getVersion() {
		return version;
	}
}
