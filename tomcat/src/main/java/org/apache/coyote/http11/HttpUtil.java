package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

	private HttpUtil() {

	}

	public static Map<String, String> parseQueryString(String body) {
		Map<String, String> parameters = new HashMap<>();
		String[] pairs = body.split("&");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=", 2);

			String key = keyValue[0];
			String value = keyValue.length > 1 ? keyValue[1] : "";

			parameters.put(key, value);
		}

		return parameters;
	}
}
