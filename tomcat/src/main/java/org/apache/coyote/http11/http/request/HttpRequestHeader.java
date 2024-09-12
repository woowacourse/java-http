package org.apache.coyote.http11.http.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.http.BaseHttpHeaders;

public class HttpRequestHeader extends BaseHttpHeaders {

	private static final int HEADER_KEY_INDEX = 0;
	private static final int HEADER_VALUE_INDEX = 1;

	public HttpRequestHeader(Map<String, List<String>> headers) {
		super(headers);
	}

	public static HttpRequestHeader from(List<String> headers) {
		return new HttpRequestHeader(initHeaders(headers));
	}

	private static Map<String, List<String>> initHeaders(List<String> headers) {
		if (headers == null || headers.isEmpty()) {
			return null;
		}

		LinkedHashMap<String, List<String>> result = new LinkedHashMap<>();
		headers.forEach(h -> {
			String[] headerParts = h.split(HEADER_DELIMITER);
			result.put(headerParts[HEADER_KEY_INDEX], parseHeaderValue(headerParts[HEADER_VALUE_INDEX]));
		});

		return result;
	}

	private static List<String> parseHeaderValue(String headerPart) {
		return Arrays.stream(headerPart.split(HEADER_VALUE_DELIMITER))
			.map(String::strip)
			.toList();
	}
}
