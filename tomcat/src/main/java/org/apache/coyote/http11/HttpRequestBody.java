package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestBody {

	private final Map<String, String> body;

	public static HttpRequestBody from(String body) {
		if (body.isEmpty()) {
			throw new IllegalArgumentException("Request body is Empty");
		}
		Map<String, String> parsedBody = HttpUtil.parseQueryString(body);
		return new HttpRequestBody(parsedBody);
	}

	private HttpRequestBody(Map<String, String> body) {
		this.body = body;
	}

	public static HttpRequestBody empty() {
		return new HttpRequestBody(null);
	}

	public String get(String key) {
		if (body == null) {
			throw new IllegalArgumentException("Request body is not initialized");
		}
		return body.get(key);
	}
}
