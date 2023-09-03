package org.apache.coyote.http11.headers;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

	private static final String HEADER_DELIMITER = ":";
	private final Map<String, String> headers;

	public HttpHeaders() {
		headers = new LinkedHashMap<>();
	}

	public HttpHeaders(final Map<String, String> headers) {
		this.headers = new LinkedHashMap<>(headers);
	}

	public static HttpHeaders from(final String httpRequest) {
		final Map<String, String> headerMaps = stream(httpRequest.split(System.lineSeparator()))
			.skip(1)
			.map(headerLine -> headerLine.split(HEADER_DELIMITER, 2))
			.collect(toMap(
				header -> header[0].trim(),
				header -> header[1].trim()
			));
		return new HttpHeaders(headerMaps);
	}

	public String build() {
		return headers.entrySet().stream()
			.map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
			.collect(joining(System.lineSeparator()))
			+ System.lineSeparator();
	}

	public void put(final String key, final String value) {
		headers.put(key, value);
	}

	public Optional<String> get(final String key) {
		return Optional.ofNullable(headers.get(key));
	}
}
