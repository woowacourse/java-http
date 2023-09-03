package org.apache.coyote.http11.headers;

import static java.util.stream.Collectors.*;

import java.util.Map;
import java.util.stream.Stream;

public class HttpCookie {

	private static final String COOKIE_DELIMITER = "; ";
	private static final String KEY_VALUE_DELIMITER = "=";

	private final Map<String, String> cookies;

	public HttpCookie(final Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public static HttpCookie from(final String cookieValue) {
		final Map<String, String> cookies = Stream.of(cookieValue.split(COOKIE_DELIMITER))
			.map(cookie -> cookie.split(KEY_VALUE_DELIMITER))
			.collect(toMap(
				values -> values[0],
				values -> values[1]
			));
		return new HttpCookie(cookies);
	}

	public Map<String, String> getCookies() {
		return cookies;
	}
}
