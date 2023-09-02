package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class QueryParam {

	private static final String QUERY_START_CHAR = "?";
	private static final String QUERY_DELIMITER = "&";
	private static final String KEY_VALUE_DELIMITER = "=";
	private static final int KEY_INDEX = 0;
	private static final int VALUE_INDEX = 1;

	private final Map<String, Set<String>> multiMap;

	public QueryParam(final Map<String, Set<String>> multiMap) {
		this.multiMap = multiMap;
	}

	public static QueryParam from(final String uri) {
		final int queryStartIndex = uri.indexOf(QUERY_START_CHAR);
		final String queryString = uri.substring(queryStartIndex + VALUE_INDEX);

		if (queryString.isBlank()) {
			return new QueryParam(Map.of());
		}

		final Map<String, Set<String>> result = Arrays.stream(queryString.split(QUERY_DELIMITER))
			.map(str -> str.split(KEY_VALUE_DELIMITER))
			.collect(groupingBy(
				query -> query[KEY_INDEX],
				mapping(
					query -> query[VALUE_INDEX],
					toSet()
				)
			));
		return new QueryParam(result);
	}
}
