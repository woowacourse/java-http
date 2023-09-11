package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.coyote.Cookie;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.response.HeaderType;

public class Http11RequestHeaderParser {

	private static final String HEADER_SEPARATOR = ": ";
	private static final String COOKIE_DELIMITER = "; ";
	private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";

	public static RequestHeader parse(final List<String> requestHeaders) {
		final var headers = requestHeaders.stream()
			.map(header -> header.split(HEADER_SEPARATOR, 2))
			.collect(toMap(parts -> parts[0], parts -> parts[1]));
		final var cookie = headers.remove(HeaderType.COOKIE.getName());
		return new RequestHeader(headers, parseCookies(cookie));
	}

	private static List<Cookie> parseCookies(final String cookieString) {
		if (cookieString == null) {
			return new ArrayList<>();
		}
		return Arrays.stream(cookieString.split(COOKIE_DELIMITER))
			.map(field -> field.split(COOKIE_KEY_VALUE_SEPARATOR))
			.filter(parts -> parts.length == 2)
			.map(parts -> new Cookie(parts[0], parts[1]))
			.collect(toList());
	}

}
