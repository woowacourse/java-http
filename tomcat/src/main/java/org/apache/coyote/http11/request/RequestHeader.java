package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {

	private static final String HEADER_SEPARATOR = ": ";

	private final Map<String, String> headers;

	private RequestHeader() {
		this(Collections.emptyMap());
	}

	private RequestHeader(final Map<String, String> headers) {
		this.headers = headers;
	}

	public static RequestHeader empty() {
		return new RequestHeader();
	}

	public static RequestHeader from(final List<String> requestHeader) {
		final var headers = requestHeader.stream()
			.map(header -> header.split(HEADER_SEPARATOR, 2))
			.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
		return new RequestHeader(headers);
	}

	public String get(final String key) {
		return headers.get(key);
	}
}
