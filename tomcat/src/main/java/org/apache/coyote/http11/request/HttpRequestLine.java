package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpProtocolVersion;

public class HttpRequestLine {

	private static final String DELIMITER = " ";

	private final HttpMethod method;
	private final HttpRequestUri uri;
	private final HttpProtocolVersion version;

	private HttpRequestLine(final HttpMethod method, final HttpRequestUri uri, final HttpProtocolVersion version) {
		this.method = method;
		this.uri = uri;
		this.version = version;
	}

	public static HttpRequestLine from(String requestLine) {
		final var parts = requestLine.split(DELIMITER);
		final var method = HttpMethod.valueOf(parts[0]);
		final var uri = HttpRequestUri.from(parts[1]);
		final var version = HttpProtocolVersion.version(parts[2]);

		return new HttpRequestLine(method, uri, version);
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
}
