package org.apache.coyote.http11.response;

import java.util.Map;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.VersionOfProtocol;

public class HttpResponse {
	private final VersionOfProtocol versionOfProtocol;
	private final StatusCode statusCode;
	private final StatusMessage statusMessage;
	private final Headers headers;
	private Body body;

	public HttpResponse(
		String versionOfProtocol,
		int statusCode,
		String statusMessage,
		Map<String, String> headers
	) {
		this.versionOfProtocol = new VersionOfProtocol(versionOfProtocol);
		this.statusCode = new StatusCode(statusCode);
		this.statusMessage = new StatusMessage(statusMessage);
		this.headers = new Headers(headers);
	}

	public HttpResponse(
		String versionOfProtocol,
		int statusCode,
		String statusMessage,
		Map<String, String> headers,
		String body
	) {
		this.versionOfProtocol = new VersionOfProtocol(versionOfProtocol);
		this.statusCode = new StatusCode(statusCode);
		this.statusMessage = new StatusMessage(statusMessage);
		this.headers = new Headers(headers);
		this.body = new Body(body);
	}

	public String toPlainText() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s %d %s \r\n", versionOfProtocol.value(), statusCode.value(), statusMessage.value()));
		headers.getHeaders().forEach((key, value) -> {
			sb.append(String.format("%s: %s\r\n", key, value));
		});
		sb.append("\r\n");
		return sb.toString();
	}
}
