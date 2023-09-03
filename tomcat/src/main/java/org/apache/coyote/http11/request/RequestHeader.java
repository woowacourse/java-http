package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.http11.Cookies;

public class RequestHeader {

	private static final String HEADER_SEPARATOR = ": ";

	private final Map<String, String> headers = new HashMap<>();
	private final Cookies cookies;

	public RequestHeader(final Map<String, String> headers, final Cookies cookies) {
		this.headers.putAll(headers);
		this.cookies = cookies;
	}

	public static RequestHeader from(final List<String> requestHeader) {
		final var headers = requestHeader.stream()
			.map(header -> header.split(HEADER_SEPARATOR, 2))
			.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
		final var cookie = headers.remove("Cookie");
		return new RequestHeader(headers, Cookies.from(cookie));
	}

	public String find(final String key) {
		return headers.get(key);
	}

	public String findCookie(final String key) {
		return cookies.find(key);
	}

	public String findSession() {
		return cookies.findSession();
	}
}
