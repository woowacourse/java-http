package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

	private static final String DELIMITER = "; ";
	private static final String KEY_VALUE_SEPARATOR = "=";
	private static final String SESSION_ID_KEY = "JSESSIONID";

	private final Map<String, String> cookies = new HashMap<>();

	private HttpCookies() {
	}

	private HttpCookies(final Map<String, String> cookies) {
		this.cookies.putAll(cookies);
	}

	public static HttpCookies empty() {
		return new HttpCookies();
	}

	public static HttpCookies from(final String cookieString) {
		if (cookieString == null) {
			return HttpCookies.empty();
		}
		return Arrays.stream(cookieString.split(DELIMITER))
			.map(field -> field.split(KEY_VALUE_SEPARATOR))
			.filter(parts -> parts.length == 2)
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(parts -> parts[0], field -> field[1]),
				HttpCookies::new
			));
	}

	public void addSession(final String sessionId) {
		cookies.put(SESSION_ID_KEY, sessionId);
	}

	public String findSession() {
		return cookies.get(SESSION_ID_KEY);
	}

	public String find(final String key) {
		return cookies.get(key);
	}

	public String format() {
		return cookies.entrySet().stream()
			.map(cookie -> cookie.getKey() + KEY_VALUE_SEPARATOR + cookie.getValue() + DELIMITER)
			.collect(Collectors.joining());
	}
}
