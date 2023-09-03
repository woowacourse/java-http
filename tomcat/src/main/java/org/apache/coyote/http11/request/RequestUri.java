package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestUri {

	private static final String QUERY_STRING_SEPARATOR = "?";
	private static final String QUERY_PARAM_DELIMITER = "&";
	private static final String KEY_VALUE_SEPARATOR = "=";

	private final String path;
	private final Map<String, String> queryParams = new HashMap<>();

	private RequestUri(final String path) {
		this(path, new HashMap<>());
	}

	private RequestUri(final String path, final Map<String, String> queryParams) {
		this.path = path;
		this.queryParams.putAll(queryParams);
	}

	public static RequestUri from(String uri) {
		final var queryStringIndex = uri.indexOf(QUERY_STRING_SEPARATOR);

		if (hasNoQuery(queryStringIndex)) {
			return new RequestUri(uri);
		}

		final var path = uri.substring(0, queryStringIndex);
		final var queryString = uri.substring(queryStringIndex + 1);
		final var queryParams = parseQueryString(queryString);

		return new RequestUri(path, queryParams);
	}

	private static Map<String, String> parseQueryString(final String queryString) {
		return Arrays.stream(queryString.split(QUERY_PARAM_DELIMITER))
			.map(queryParam -> queryParam.split(KEY_VALUE_SEPARATOR, 2))
			.filter(parts -> parts.length == 2)
			.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
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
