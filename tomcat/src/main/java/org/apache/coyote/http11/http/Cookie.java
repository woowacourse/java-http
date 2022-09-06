package org.apache.coyote.http11.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Cookie {

	private static final String COOKIE_DELIMITER = "; ";
	private static final String KEY_VALUE_DELIMITER = "=";

	private static final int KEY_INDEX = 0;
	private static final int VALUE_INDEX = 1;
	private static final String NOT_EXIST_VALUE = "";

	private final Map<String, String> values = new HashMap<>();

	public Cookie() {
	}

	public Cookie(String key, String value) {
		values.put(key, value);
	}

	public Cookie(String stringValue) {
		splitCookies(stringValue);
	}

	private void splitCookies(String stringValue) {
		String[] cookies = stringValue.split(COOKIE_DELIMITER);
		for (String cookie : cookies) {
			addCookie(cookie);
		}
	}

	private void addCookie(String cookie) {
		if (cookie.contains(KEY_VALUE_DELIMITER)) {
			String[] keyValue = cookie.split(KEY_VALUE_DELIMITER);
			values.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
		}
	}

	public String values() {
		List<String> cookies = new ArrayList<>();
		for (String key : values.keySet()) {
			cookies.add(key + KEY_VALUE_DELIMITER + values.get(key));
		}
		return String.join(COOKIE_DELIMITER, cookies);
	}

	public String getValue(String key) {
		return Optional.ofNullable(values.get(key))
			.orElse(NOT_EXIST_VALUE);
	}
}
