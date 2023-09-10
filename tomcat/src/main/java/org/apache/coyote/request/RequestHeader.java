package org.apache.coyote.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.Cookie;

public class RequestHeader {

	private final Map<String, String> headers = new HashMap<>();
	private final List<Cookie> cookies;

	public RequestHeader(Map<String, String> headers, final List<Cookie> cookies) {
		this.headers.putAll(headers);
		this.cookies = cookies;
	}

	public String find(final String key) {
		return headers.get(key);
	}

	public String findCookie(final String key) {
		return cookies.stream()
			.filter(cookie -> Objects.equals(key, cookie.getKey()))
			.findAny()
			.map(Cookie::getValue)
			.orElse(null);
	}

	public String findSession() {
		return cookies.stream()
			.filter(Cookie::isSession)
			.findAny()
			.map(Cookie::getValue)
			.orElse(null);
	}
}
