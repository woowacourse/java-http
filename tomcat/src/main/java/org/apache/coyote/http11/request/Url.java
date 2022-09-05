package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.exception.ExceptionType;
import nextstep.jwp.exception.InvalidHttpRequestException;

public class Url {

	private static final String START_QUERY_STRING_DELIMITER = "?";
	private static final String QUERY_CONNECT_DELIMITER = "&";
	private static final String QUERY_VALUE_DELIMITER = "=";
	private static final int NAME_INDEX = 0;
	private static final int VALUE_INDEX = 1;
	private static final String GAP = " ";

	private final String path;
	private Map<String, String> params;

	private Url(String value, Map<String, String> params) {
		validate(value);
		this.path = value;
		this.params = new HashMap<>(params);
	}

	public static Url from(String url) {
		return new Url(findUrl(url), findParam(url));
	}

	private static String findUrl(String url) {
		return url.split(GAP)[0];
	}

	private void validate(String path) {
		if (path.isEmpty()) {
			throw new InvalidHttpRequestException(ExceptionType.INVALID_URL_EXCEPTION);
		}
	}

	private static Map<String, String> findParam(String url) {
		int startQueryIndex = findStartQueryString(url);
		if (startQueryIndex < 0) {
			return new HashMap<>();
		}
		String queryString = extractQueryString(url, startQueryIndex);
		return parseQueryString(queryString);
	}

	private static int findStartQueryString(String url) {
		return url.indexOf(START_QUERY_STRING_DELIMITER);
	}

	private static String extractQueryString(String url, int startQueryIndex) {
		return url.substring(startQueryIndex + VALUE_INDEX);
	}

	private static Map<String, String> parseQueryString(String queryString) {
		String[] values = queryString.split(QUERY_CONNECT_DELIMITER);
		Map<String, String> params = new HashMap<>();
		for (String param : values) {
			final String[] data = param.split(QUERY_VALUE_DELIMITER);
			params.put(data[NAME_INDEX], data[VALUE_INDEX]);
		}
		return params;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParams() {
		return params;
	}
}
