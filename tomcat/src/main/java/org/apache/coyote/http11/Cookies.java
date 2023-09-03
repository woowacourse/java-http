package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

	private static final String DELIMITER = "; ";
	private static final String KEY_VALUE_SEPARATOR = "=";
	private static final String SESSION_ID_KEY = "JSESSIONID";

	private final Map<String, String> cookies = new HashMap<>();

	private Cookies() {
	}

	private Cookies(final Map<String, String> cookies) {
		this.cookies.putAll(cookies);
	}

	public static Cookies empty() {
		return new Cookies();
	}

	public static Cookies from(final String cookieString) {
		if (cookieString == null) {
			return Cookies.empty();
		}
		return Arrays.stream(cookieString.split(DELIMITER))
			.map(field -> field.split(KEY_VALUE_SEPARATOR))
			.filter(parts -> parts.length == 2)
			.collect(collectingAndThen(toMap(parts -> parts[0], field -> field[1]), Cookies::new));
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
