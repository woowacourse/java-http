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

		return Arrays.stream(cookies).collect(Collectors.collectingAndThen(
			Collectors.toMap(
				c -> c.split(KEY_VALUE_DELIMITER)[KEY_INDEX],
				c -> c.split(KEY_VALUE_DELIMITER)[VALUE_INDEX]
			),
			HttpCookie::new));
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
		return getValue(JSESSIONID);
	}
}
