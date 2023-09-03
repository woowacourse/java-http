package org.apache.coyote.http11.response;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.http11.Cookies;

public class ResponseHeader {

	private static final String CRLF = "\r\n";
	private static final String HEADER_SEPARATOR = ": ";
	private static final String LINE_END = " ";

	private final Map<ResponseHeaderType, String> headers;

	public ResponseHeader(final Map<ResponseHeaderType, String> headers) {
		this.headers = headers;
	}

	public void add(ResponseHeaderType key, String value) {
		headers.put(key, value);
	}

	public void addCookies(Cookies cookies) {
		headers.put(ResponseHeaderType.SET_COOKIE, cookies.format());
	}

	public String formatHeader() {
		return headers.entrySet().stream()
			.sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
			.map(e -> String.join(HEADER_SEPARATOR, e.getKey().getName(), e.getValue()) + LINE_END)
			.collect(Collectors.joining(CRLF));
	}
}
