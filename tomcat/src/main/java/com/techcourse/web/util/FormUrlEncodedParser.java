package com.techcourse.web.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormUrlEncodedParser {

	private static final String PARAMETER_SEPARATOR = "&";
	private static final String KEY_VALUE_SEPARATOR = "=";
	private static final int KEY_INDEX = 0;
	private static final int VALUE_INDEX = 1;

	public static Map<String, String> parse(String body) {
		validateBodyIsNotEmpty(body);
		String[] pairs = body.split(PARAMETER_SEPARATOR);

		return Arrays.stream(pairs)
			.collect(Collectors.toMap(
				pair -> pair.split(KEY_VALUE_SEPARATOR)[KEY_INDEX],
				FormUrlEncodedParser::getValue
			));
	}

	private static void validateBodyIsNotEmpty(String body) {
		if (body == null || body.isBlank()) {
			throw new IllegalArgumentException("Body is empty");
		}
	}

	private static String getValue(String pair) {
		String[] keyValue = pair.split(KEY_VALUE_SEPARATOR);
		if (keyValue.length == 1) {
			return "";
		}
		return keyValue[VALUE_INDEX];
	}
}
