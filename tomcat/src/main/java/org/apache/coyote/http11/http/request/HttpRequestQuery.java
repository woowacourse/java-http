package org.apache.coyote.http11.http.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestQuery {

	private static final String QUERY_PARAM_DELIMITER = "&";
	private static final String QUERY_KEY_VALUE_DELIMITER = "=";
	private static final int QUERY_KEY_INDEX = 0;
	private static final int QUERY_VALUE_INDEX = 1;

	private final Map<String, String> query;

	public HttpRequestQuery(Map<String, String> query) {
		this.query = query;
	}

	public static HttpRequestQuery from(String query) {
		return new HttpRequestQuery(initQuery(query));
	}

	private static Map<String, String> initQuery(String query) {
		if (query == null || query.isBlank()) {
			return null;
		}

		return Arrays.stream(query.split(QUERY_PARAM_DELIMITER))
			.collect(Collectors.toMap(HttpRequestQuery::parseKey, HttpRequestQuery::parseValue));
	}

	private static String parseKey(String queryPart) {
		return queryPart.split(QUERY_KEY_VALUE_DELIMITER)[QUERY_KEY_INDEX];
	}

	private static String parseValue(String queryPart) {
		String[] parts = queryPart.split(QUERY_KEY_VALUE_DELIMITER);
		if (parts.length == 1) {
			return "";
		}
		return parts[QUERY_VALUE_INDEX];
	}

	public String getValue(String key) {
		if (isNotExistKey(key)) {
			throw new IllegalArgumentException("Key not found. key: " + key);
		}
		return query.get(key);
	}

	private boolean isNotExistKey(String key) {
		if (query == null) {
			return false;
		}
		return query.containsKey(key);
	}
}
