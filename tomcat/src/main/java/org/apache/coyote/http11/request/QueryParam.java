package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.util.QueryParser;

public class QueryParam {

	private final Map<String, String> paramMap;

	public QueryParam(final Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public static QueryParam from(final String query) {
		if (query.isEmpty()) {
			return new QueryParam(Map.of());
		}

		final Map<String, String> result = QueryParser.parse(query);
		return new QueryParam(result);
	}

	public String get(final String key) {
		return paramMap.get(key);
	}

	public boolean isBlank() {
		return paramMap.size() == 0;
	}
}
