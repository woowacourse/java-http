package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

	private static final String KEY_VALUE_DELIMITER = ": ";
	private static final int KEY = 0;
	private static final int VALUE = 1;

	private final Map<String, String> value;

	private HttpHeaders(final Map<String, String> value) {
		this.value = value;
	}

	public static HttpHeaders from(final List<String> messages) {
		final Map<String, String> headers = new HashMap<>();
		for (final String message : messages) {
			final String[] headerElement = message.split(KEY_VALUE_DELIMITER);
			headers.put(headerElement[KEY], headerElement[VALUE]);
		}
		return new HttpHeaders(headers);
	}
}
