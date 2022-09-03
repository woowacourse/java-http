package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
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

	public static HttpHeaders init() {
		return new HttpHeaders(new LinkedHashMap<>());
	}

	public static HttpHeaders from(final List<String> messages) {
		final Map<String, String> headers = new LinkedHashMap<>();
		for (final String message : messages) {
			final String[] headerElement = message.split(KEY_VALUE_DELIMITER);
			headers.put(headerElement[KEY], headerElement[VALUE]);
		}
		return new HttpHeaders(headers);
	}

	public HttpHeaders add(final String key, final String value) {
		this.value.put(key, value);
		return this;
	}

	public String toMessage() {
		final StringBuilder message = new StringBuilder();
		for (Map.Entry<String, String> entry : value.entrySet()) {
			message.append(entry.getKey())
				.append(KEY_VALUE_DELIMITER)
				.append(entry.getValue())
				.append(" ")
				.append("\r\n");
		}
		excludeLastEmpty(message);
		return new String(message);
	}

	private void excludeLastEmpty(final StringBuilder message) {
		message.deleteCharAt(message.length() - 1);
		message.deleteCharAt(message.length() - 1);
	}
}
