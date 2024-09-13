package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.HeaderKey.*;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.HeaderKey;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.session.Session;

import jakarta.servlet.http.Cookie;

public class HttpRequest {
	private final Method method;
	private final Path path;
	private final Headers headers;
	private final Body body;

	public HttpRequest(BufferedReader reader) throws IOException {
		String requestLine = reader.readLine();
		this.method = Method.request(requestLine);
		this.path = Path.request(requestLine);
		this.headers = Headers.parseRequestHeader(reader);
		this.body = Body.request(headers, reader);
	}

	public Method getMethod() {
		return method;
	}

	public Path getPath() {
		return path;
	}

	public Body getBody() {
		return body;
	}

	public Session getSession() {
		String cookie = headers.getValue(COOKIE);
		if (cookie == null) {
			return null;
		}
		return new Session(cookie);
	}
}
