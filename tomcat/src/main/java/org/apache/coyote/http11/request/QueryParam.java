package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Map;

public class QueryParam {

	private static final String QUERY_START_CHAR = "?";
	private static final String QUERY_DELIMITER = "&";
	private static final String KEY_VALUE_DELIMITER = "=";
	private static final int KEY_INDEX = 0;
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

		final Map<String, String> result = Arrays.stream(queryString.split(QUERY_DELIMITER))
			.map(str -> str.split(KEY_VALUE_DELIMITER))
			.collect(toMap(
				parsed -> parsed[KEY_INDEX],
				parsed -> parsed[VALUE_INDEX]
			));
		return new QueryParam(result);
	}

	public String get(final String key) {
		return paramMap.get(key);
	}
}
