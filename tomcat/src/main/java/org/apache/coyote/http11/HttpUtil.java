package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

	private static final String MULTI_ITEM_DELIMITER = "&";
	private static final String KEY_VALUE_DELIMITER = "=";
	private static final int KEY_VALUE_SPLIT_LIMIT = 2;
	public static final int KEY_INDEX = 0;
	public static final int VALUE_INDEX = 1;

	private HttpUtil() {

	}

	public static Map<String, String> parseQueryString(String body) {
		Map<String, String> parameters = new HashMap<>();
		String[] pairs = body.split(MULTI_ITEM_DELIMITER);

		for (String pair : pairs) {
			String[] keyValue = pair.split(KEY_VALUE_DELIMITER, KEY_VALUE_SPLIT_LIMIT);

			String key = keyValue[KEY_INDEX];
			String value = keyValue[VALUE_INDEX];

			parameters.put(key, value);
		}

		return parameters;
	}
}
