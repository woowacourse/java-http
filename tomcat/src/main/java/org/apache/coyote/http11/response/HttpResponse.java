package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.VersionOfProtocol;

public class HttpResponse {
	private VersionOfProtocol versionOfProtocol;
	private StatusCode statusCode;
	private StatusMessage statusMessage;
	private Headers headers;
	private Body body;

	public HttpResponse() {
		this.headers = new Headers();
	}

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
		headers.headers().forEach((key, value) -> {
			sb.append(String.format("%s: %s \r\n", key, value));
		});
		sb.append("\r\n");
		if (body != null) {
			sb.append(body.value());
			// sb.append("\r\n");
		}
		return sb.toString();
	}

	public void setVersionOfProtocol(String versionOfProtocol) {
		this.versionOfProtocol = new VersionOfProtocol(versionOfProtocol);
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = new StatusCode(statusCode);
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = new StatusMessage(statusMessage);
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = new Headers(headers);
	}

	public void setBody(String filename) throws IOException {
		try {
			URL url = HttpResponse.class.getClassLoader().getResource(filename);
			File file = new File(url.getPath());
			this.body = new Body(new String(Files.readAllBytes(file.toPath())));

			ContentType contentType = ContentType.fromPath(url.getPath());
			this.headers.add("Content-Type", contentType.getValue() + ";charset=utf-8");
			this.headers.add("Content-Length", String.valueOf(body.getContentLength()));
		} catch (AccessDeniedException | NullPointerException exception) {
			this.body = new Body("Hello, World!");

			this.headers.add("Content-Type", "text/html;charset=utf-8");
			this.headers.add("Content-Length", String.valueOf(body.getContentLength()));
		}
	}
}
