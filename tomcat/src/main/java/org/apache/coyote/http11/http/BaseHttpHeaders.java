package org.apache.coyote.http11.http;

import java.util.List;
import java.util.Map;

public abstract class BaseHttpHeaders {

	protected static final String HEADER_DELIMITER = ": ";
	protected static final String HEADER_VALUE_DELIMITER = ", ";

	protected final Map<String, List<String>> headers;

	public BaseHttpHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public List<String> getValue(String key) {
		if (isNotExistKey(key)) {
			throw new IllegalArgumentException("Key not found. key: " + key);
		}
		return headers.get(key);
	}

	private boolean isNotExistKey(String key) {
		if (headers == null) {
			return false;
		}
		return !headers.containsKey(key);
	}
}
