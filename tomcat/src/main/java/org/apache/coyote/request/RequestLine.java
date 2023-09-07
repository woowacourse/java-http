package org.apache.coyote.request;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpProtocolVersion;

public class RequestLine {

	private final HttpMethod method;
	private final RequestUri uri;
	private final HttpProtocolVersion version;

	public RequestLine(final HttpMethod method, final RequestUri uri, final HttpProtocolVersion version) {
		this.method = method;
		this.uri = uri;
		this.version = version;
	}

	public boolean hasPath(final String path) {
		return uri.hasPath(path);
	}

	public boolean hasMethod(final HttpMethod method) {
		return this.method.equals(method);
	}

	public String findQueryParam(final String key) {
		return uri.findQueryParam(key);
	}

	public String getPath() {
		return uri.getPath();
	}
}
