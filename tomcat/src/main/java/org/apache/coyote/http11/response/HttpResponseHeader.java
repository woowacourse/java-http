package org.apache.coyote.http11.response;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {

	private static final String CRLF = "\r\n";

	private static final String HEADER_SEPARATOR = ": ";
	private static final String LINE_END = " ";

	private final Map<HttpResponseHeaderType, String> headers;

	public HttpResponseHeader(final Map<HttpResponseHeaderType, String> headers) {
		this.headers = headers;
	}

	public String formatHeader() {
		return headers.entrySet().stream()
			.sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
			.map(e -> String.join(HEADER_SEPARATOR, e.getKey().getName(), e.getValue()) + LINE_END)
			.collect(Collectors.joining(CRLF));
	}
}
