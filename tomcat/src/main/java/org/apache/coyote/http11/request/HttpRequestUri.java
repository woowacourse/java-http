package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequestUri {

	private static final String QUERY_STRING_SEPARATOR = "?";
	private static final String QUERY_PARAM_DELIMITER = "&";
	private static final String KEY_VALUE_SEPARATOR = "=";

	private final String path;
	private final Map<String, String> queryParams;

	private HttpRequestUri(final String path) {
		this(path, Collections.emptyMap());
	}

	private HttpRequestUri(final String path, final Map<String, String> queryParams) {
		this.path = path;
		this.queryParams = queryParams;
	}

	public static HttpRequestUri from(String uri) {
		final var queryStringIndex = uri.indexOf(QUERY_STRING_SEPARATOR);

		if (hasNoQuery(queryStringIndex)) {
			return new HttpRequestUri(uri);
		}

		final var path = uri.substring(0, queryStringIndex);
		final var queryString = uri.substring(queryStringIndex + 1);
		final var queryParams = parseQueryString(queryString);

		return new HttpRequestUri(path, queryParams);
	}

	private static Map<String, String> parseQueryString(final String queryString) {
		final Map<String, String> queryParams = new HashMap<>();

		Arrays.stream(queryString.split(QUERY_PARAM_DELIMITER))
			.map(queryParam -> queryParam.split(KEY_VALUE_SEPARATOR, 2))
			.filter(parts -> parts.length == 2)
			.forEach(parts -> queryParams.put(parts[0], parts[1]));

		return queryParams;
	}

	private static boolean hasNoQuery(final int index) {
		return index == -1;
	}

	public boolean hasPath(final String path) {
		return Objects.equals(this.path, path);
	}

	public String findQueryParam(final String key) {
		return queryParams.get(key);
	}

	public String getPath() {
		return path;
	}
}
