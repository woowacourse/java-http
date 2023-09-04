package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.util.QueryParser;

public class QueryParam {

	private static final String QUERY_START_CHAR = "?";
	private static final int VALUE_INDEX = 1;

	private final Map<String, String> paramMap;

	public QueryParam(final Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public static QueryParam from(final String uri) {
		final int queryStartIndex = uri.indexOf(QUERY_START_CHAR);
		final String queryString = uri.substring(queryStartIndex + VALUE_INDEX);

		if (queryStartIndex == -1) {
			return new QueryParam(Map.of());
		}

		final Map<String, String> result = QueryParser.parse(queryString);
		return new QueryParam(result);
	}

	public String get(final String key) {
		return paramMap.get(key);
	}

	public boolean isBlank() {
		return paramMap.size() == 0;
	}
}
