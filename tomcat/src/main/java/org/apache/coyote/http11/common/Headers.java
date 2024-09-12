package org.apache.coyote.http11.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record Headers(Map<String, String> headers) {

	public Headers() {
		this(new HashMap<>());
	}

	public static Headers request(BufferedReader reader) throws IOException {
		HashMap<String, String> headers = new HashMap<>();
		String line;
		while ((line = reader.readLine()) != null && !line.isEmpty()) {
			String[] header = line.split(":");
			headers.put(header[0].trim(), header[1].trim());
		}
		return new Headers(headers);
	}

	public void add(String key, String value) {
		headers.put(key, value);
	}

	public String getValue(String key) {
		return headers.get(key);
	}
}
