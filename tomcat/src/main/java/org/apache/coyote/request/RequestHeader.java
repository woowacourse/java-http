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
		final var optionalCookie = cookies.stream()
			.filter(cookie -> Objects.equals(key, cookie.getKey()))
			.findAny();
		if (optionalCookie.isEmpty()) {
			return null;
		}
		return optionalCookie.get().getValue();
	}

	public String findSession() {
		final var optionalCookie = cookies.stream()
			.filter(Cookie::isSession)
			.findAny();
		if (optionalCookie.isEmpty()) {
			return null;
		}
		return optionalCookie.get().getValue();
	}
}
