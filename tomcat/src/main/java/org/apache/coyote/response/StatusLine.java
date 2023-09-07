package org.apache.coyote.response;

import org.apache.coyote.HttpProtocolVersion;

public class StatusLine {

	private final HttpProtocolVersion version;
	private final StatusCode code;

	public StatusLine(final StatusCode code) {
		this.version = HttpProtocolVersion.HTTP11;
		this.code = code;
	}

	public HttpProtocolVersion getVersion() {
		return version;
	}

	public StatusCode getCode() {
		return code;
	}
}
