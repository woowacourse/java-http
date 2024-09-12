package org.apache.coyote.http11.http.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.coyote.http11.http.BaseHttpHeaders;

public class HttpResponseHeader extends BaseHttpHeaders {

	public HttpResponseHeader() {
		super(new LinkedHashMap<>());
	}

	public void addHeader(String key, String value) {
		if (headers.containsKey(key)) {
			headers.get(key).add(value);
			return;
		}
		addHeader(key, List.of(value));
	}

	public void addHeader(String key, List<String> values) {
		if (headers.containsKey(key)) {
			headers.get(key).addAll(values);
			return;
		}
		headers.put(key, new ArrayList<>(values));
	}

	public String toResponseMessage() {
		List<String> lines = new ArrayList<>();
		headers.forEach((key, value) -> {
			String values = String.join(HEADER_VALUE_DELIMITER, value);
			lines.add(key + HEADER_DELIMITER + values + " ");
		});

		return String.join("\r\n", lines);
	}
}
