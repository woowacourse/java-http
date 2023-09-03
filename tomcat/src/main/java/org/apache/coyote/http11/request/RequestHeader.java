package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.http11.HttpCookies;

public class RequestHeader {

	private static final String HEADER_SEPARATOR = ": ";

	private final Map<String, String> headers = new HashMap<>();
	private final HttpCookies cookie;

	public RequestHeader(final Map<String, String> headers, final HttpCookies cookie) {
		this.headers.putAll(headers);
		this.cookie = cookie;
	}

	public static RequestHeader from(final List<String> requestHeader) {
		final var headers = requestHeader.stream()
			.map(header -> header.split(HEADER_SEPARATOR, 2))
			.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
		final var cookie = headers.remove("Cookie");
		return new RequestHeader(headers, HttpCookies.from(cookie));
	}

	public String find(final String key) {
		return headers.get(key);
	}

	public String findCookie(final String key) {
		return cookie.find(key);
	}
}
