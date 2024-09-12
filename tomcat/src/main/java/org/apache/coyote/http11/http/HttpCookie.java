package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

	public static final String JSESSIONID = "JSESSIONID";
	private static final String COOKIE_VALUE_DELIMITER = "; ";
	private static final String KEY_VALUE_DELIMITER = "=";
	private static final int KEY_INDEX = 0;
	private static final int VALUE_INDEX = 1;

	private final Map<String, String> cookie;

	public static HttpCookie from(String cookieValues) {
		String[] cookies = cookieValues.split(COOKIE_VALUE_DELIMITER);

		return Arrays.stream(cookies)
			.map(cookie -> cookie.split(KEY_VALUE_DELIMITER))
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(HttpCookie::parsekey, HttpCookie::parseValue),
				HttpCookie::new)
			);
	}

	private static String parsekey(String[] cookie) {
		return cookie[KEY_INDEX];
	}

	private static String parseValue(String[] cookie) {
		if (cookie.length == 1) {
			return "";
		}
		return cookie[VALUE_INDEX];
	}

	private HttpCookie(Map<String, String> cookie) {
		this.cookie = cookie;
	}

	public String getValue(String key) {
		if (isNotExistKey(key)) {
			throw new IllegalArgumentException("Key not found. key: " + key);
		}
		return cookie.get(key);
	}

	private boolean isNotExistKey(String key) {
		return !cookie.containsKey(key);
	}

	public String getJsessionid() {
		if (isNotExistKey(JSESSIONID)) {
			return null;
		}
		return cookie.get(JSESSIONID);
	}
}
