package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpProtocolVersion;

public class RequestLine {

	private static final String DELIMITER = " ";

	private final HttpMethod method;
	private final RequestUri uri;
	private final HttpProtocolVersion version;

	private RequestLine(final HttpMethod method, final RequestUri uri, final HttpProtocolVersion version) {
		this.method = method;
		this.uri = uri;
		this.version = version;
	}

	public static RequestLine from(String requestLine) {
		final var parts = requestLine.split(DELIMITER);
		final var method = HttpMethod.valueOf(parts[0]);
		final var uri = RequestUri.from(parts[1]);
		final var version = HttpProtocolVersion.version(parts[2]);

		return new RequestLine(method, uri, version);
	}

	public boolean hasPath(final String path) {
		return uri.hasPath(path);
	}

	public String findQueryParam(final String key) {
		return uri.findQueryParam(key);
	}

	public String getPath() {
		return uri.getPath();
	}

	public boolean hasMethod(final HttpMethod method) {
		return this.method.equals(method);
	}
}
