package org.apache.coyote.http11.util;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Map;

public class QueryParser {

	private static final String QUERY_DELIMITER = "&";
	private static final String KEY_VALUE_DELIMITER = "=";
	private static final int KEY_INDEX = 0;
	private static final int VALUE_INDEX = 1;

	private QueryParser() {
	}

	public static Map<String, String> parse(final String query) {
		return Arrays.stream(query.split(QUERY_DELIMITER))
			.map(str -> str.split(KEY_VALUE_DELIMITER))
			.collect(toMap(
				parsed -> parsed[KEY_INDEX],
				parsed -> parsed[VALUE_INDEX]
			));
	}
}
