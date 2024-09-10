package org.apache.coyote.http11.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Headers {
	private final Map<String, String> headers;

	public Headers(Map<String, String> headers) {
		this.headers = headers;
	}

	public Headers(BufferedReader reader) throws IOException {
		this.headers = new HashMap<>();
		String line;
		while ((line = reader.readLine()) != null && !line.isEmpty()) {
			String[] header = line.split(":");
			headers.put(header[0].trim(), header[1].trim());
		}
	}

	public String getValue(String key) {
		return headers.get(key);
	}
}
