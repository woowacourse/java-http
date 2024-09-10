package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.VersionOfProtocol;

public class HttpResponse {
	private final VersionOfProtocol versionOfProtocol;
	private final StatusCode statusCode;
	private final StatusMessage statusMessage;
	private final Headers headers;

	public HttpResponse(
		VersionOfProtocol versionOfProtocol,
		StatusCode statusCode,
		StatusMessage statusMessage,
		Headers headers
	) {
		this.versionOfProtocol = versionOfProtocol;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.headers = headers;
	}
}
