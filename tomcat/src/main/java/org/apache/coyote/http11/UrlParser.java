package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class UrlParser {

	private static final String PARAMETER_FORMAT = "?";
	private static final String PARAMETER_REGEX = "\\?";
	private static final String HTML_EXTENSION = ".html";
	private static final int QUERIES_LOCATION = 1;
	private static final String QUERIES_REGEX = "&";
	private static final String QUERY_REGEX = "=";
	private static final int KEY = 0;
	private static final int VALUE = 1;

	private final String resource;
	private final Map<String, String> queries = new HashMap<>();

	public UrlParser(final String uri) {
		if (isNotContainQueries(uri)) {
			resource = uri;
			return;
		}
		final String[] uriElement = uri.split(PARAMETER_REGEX);
		resource = uriElement[0] + HTML_EXTENSION;
		parseQueries(uriElement);
	}

	private static boolean isNotContainQueries(String uri) {
		return !uri.contains(PARAMETER_FORMAT);
	}

	private void parseQueries(final String[] uriElement) {
		final String[] queryElements = uriElement[QUERIES_LOCATION].split(QUERIES_REGEX);
		for (String element : queryElements) {
			final String[] queryValueElement = element.split(QUERY_REGEX);
			queries.put(queryValueElement[KEY], queryValueElement[VALUE]);
		}
	}

	public String getResource() {
		return resource;
	}

	public Map<String, String> getQueries() {
		return Map.copyOf(queries);
	}
}
