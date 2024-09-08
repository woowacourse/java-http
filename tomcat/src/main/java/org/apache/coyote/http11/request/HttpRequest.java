package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

	private static final String CONTENT_LENGTH = "Content-Length";

	private final String method;
	private final String path;
	private final Map<String, String> headers = new HashMap<>();
	private final String body;

	public HttpRequest(BufferedReader reader) throws IOException {
		String initialLine = reader.readLine();
		this.method = initialLine.split(" ")[0];
		this.path = initialLine.split(" ")[1];
		String line;
		while ((line = reader.readLine()) != null && !line.isEmpty()) {
			String[] header = line.split(":");
			headers.put(header[0].trim(), header[1].trim());
		}
		this.body = parseBody(reader);
	}

	private String parseBody(BufferedReader reader) throws IOException {
		if(headers.get(CONTENT_LENGTH) == null) {
			return null;
		}
		int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
		if(contentLength > 0) {
			char[] body = new char[contentLength];
			reader.read(body, 0, contentLength);
			return new String(body);
		}
		return null;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getCookie() {
		return headers.get("Cookie");
	}

	public String getBody() {
		return body;
	}
}
