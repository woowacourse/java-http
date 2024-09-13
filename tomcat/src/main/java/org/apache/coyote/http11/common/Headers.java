package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.HttpDelimiter.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record Headers(Map<String, String> headers) {
	public Headers() {
		this(new HashMap<>());
	}

	public static Headers parseRequestHeader(BufferedReader reader) throws IOException {
		HashMap<String, String> headers = new HashMap<>();
		String line;
		while ((line = reader.readLine()) != null && !line.isEmpty()) {
			String[] header = line.split(HEADER_KEY_VALUE_DELIMITER.getValue());
			headers.put(header[0].trim(), header[1].trim());
		}
		return new Headers(headers);
	}

	public void add(HeaderKey key, String value) {
		headers.put(key.getValue(), value);
	}

	public String getValue(HeaderKey headerKey) {
		return headers.get(headerKey.getValue());
	}

	public String generatePlainText() {
		StringBuilder sb = new StringBuilder();
		headers().forEach((key, value) -> {
			sb.append(String.format("%s: %s \r\n", key, value));
		});
		return sb.toString();
	}
}
