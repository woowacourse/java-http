package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeader {

	private static final String HEADER_SEPARATOR = ": ";

	private final Map<String, String> headers;

	private HttpRequestHeader() {
		this(Collections.emptyMap());
	}

	private HttpRequestHeader(final Map<String, String> headers) {
		this.headers = headers;
	}

	public static HttpRequestHeader empty() {
		return new HttpRequestHeader();
	}

	public static HttpRequestHeader from(final List<String> requestHeader) {
		final var headers = requestHeader.stream()
			.map(header -> header.split(HEADER_SEPARATOR, 2))
			.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
		return new HttpRequestHeader(headers);
	}
}
