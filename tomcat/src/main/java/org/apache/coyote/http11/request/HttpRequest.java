package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {


	private final Method method;
	private final Path path;
	private final Headers headers;
	private final Body body;

	public HttpRequest(BufferedReader reader) throws IOException {
		String initialLine = reader.readLine();
		this.method = new Method(initialLine);
		this.path = new Path(initialLine);
		this.headers = new Headers(reader);
		this.body = new Body(headers, reader);
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
