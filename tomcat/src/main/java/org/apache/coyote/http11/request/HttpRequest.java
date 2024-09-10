package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.Headers;

public class HttpRequest {
	private final Method method;
	private final Path path;
	private final Headers headers;
	private final Body body;

	public HttpRequest(BufferedReader reader) throws IOException {
		String requestLine = reader.readLine();
		this.method = Method.request(requestLine);
		this.path = Path.request(requestLine);
		this.headers = Headers.request(reader);
		this.body = Body.request(headers, reader);
	}

	public Method getMethod() {
		return method;
	}

	public Path getPath() {
		return path;
	}

	public String getCookie() {
		return headers.getValue("Cookie");
	}

	public Body getBody() {
		return body;
	}
}
