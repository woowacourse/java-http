package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.HeaderKey.*;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Properties;
import org.apache.coyote.http11.common.VersionOfProtocol;
import org.apache.coyote.http11.session.Session;

public class HttpRequest {
	private final RequestLine requestLine;
	private final Headers headers;
	private final Body body;

	public HttpRequest(BufferedReader reader) throws IOException {
		String requestLine = reader.readLine();
		this.requestLine = new RequestLine(
			Method.parseRequestToMethod(requestLine),
			Path.parseRequestToPath(requestLine),
			VersionOfProtocol.parseReqeustToVersionOfProtocol(requestLine)
		);
		this.headers = Headers.parseRequestHeader(reader);
		this.body = Body.parseRequestBody(headers, reader);
	}

	public Method getMethod() {
		return requestLine.method();
	}

	public Path getPath() {
		return requestLine.path();
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

	public String getProperty(String key) {
		Properties properties = body.parseProperty();
		return properties.get(key);
	}
}
