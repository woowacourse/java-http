package org.apache.coyote.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.coyote.Cookie;

public class RequestHeader {

	private final Map<String, String> headers = new HashMap<>();
	private final List<Cookie> cookies = new ArrayList<>();

	private RequestHeader() {
	}

	public RequestHeader(Map<String, String> headers, final List<Cookie> cookies) {
		this.headers.putAll(headers);
		this.cookies.addAll(cookies);
	}

	public static RequestHeader empty() {
		return new RequestHeader();
	}

	public String find(final String key) {
		return headers.get(key);
	}

	public Optional<String> findCookie(final String key) {
		return cookies.stream()
			.filter(cookie -> Objects.equals(key, cookie.getKey()))
			.findAny()
			.map(Cookie::getValue);
	}

	public Optional<String> findSessionId() {
		return cookies.stream()
			.filter(Cookie::isSession)
			.findAny()
			.map(Cookie::getValue);
	}
}
