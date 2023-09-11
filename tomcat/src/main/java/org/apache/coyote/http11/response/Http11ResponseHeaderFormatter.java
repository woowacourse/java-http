package org.apache.coyote.http11.response;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import org.apache.coyote.Cookie;
import org.apache.coyote.response.HeaderType;
import org.apache.coyote.response.ResponseHeader;

public class Http11ResponseHeaderFormatter {

	private static final String CRLF = "\r\n";
	private static final String HEADER_SEPARATOR = ": ";
	private static final String COOKIE_DELIMITER = "; ";
	private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";
	private static final String LINE_END = " ";

	public static String format(final ResponseHeader responseHeader) {
		final var headers = responseHeader.getHeaders();
		final var cookies = responseHeader.getCookies();
		if (!cookies.isEmpty()) {
			headers.put(HeaderType.SET_COOKIE, format(cookies));
		}
		return headers.entrySet().stream()
			.sorted(comparingInt(e -> e.getKey().ordinal()))
			.map(e -> String.join(HEADER_SEPARATOR, e.getKey().getName(), e.getValue()) + LINE_END)
			.collect(joining(CRLF));
	}

	private static String format(final List<Cookie> cookies) {
		return cookies.stream()
			.map(cookie -> cookie.getKey() + COOKIE_KEY_VALUE_SEPARATOR + cookie.getValue() + COOKIE_DELIMITER)
			.collect(joining());
	}
}
