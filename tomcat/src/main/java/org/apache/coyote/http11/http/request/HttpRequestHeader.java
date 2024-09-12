package org.apache.coyote.http11.http.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.http.BaseHttpHeaders;
import org.apache.coyote.http11.http.HttpCookie;

public class HttpRequestHeader extends BaseHttpHeaders {

	private static final int HEADER_KEY_INDEX = 0;
	private static final int HEADER_VALUE_INDEX = 1;

	private final HttpCookie httpCookie;

	public HttpRequestHeader(Map<String, List<String>> headers, HttpCookie httpCookie) {
		super(headers);
		this.httpCookie = httpCookie;
	}

	public static HttpRequestHeader from(List<String> headers) {
		return new HttpRequestHeader(initHeaders(headers), initCookie(headers));
	}

	private static Map<String, List<String>> initHeaders(List<String> headers) {
		if (headers == null || headers.isEmpty()) {
			return null;
		}

		LinkedHashMap<String, List<String>> result = new LinkedHashMap<>();
		headers.stream()
			.filter(header -> !header.startsWith("Cookie"))
			.forEach(h -> {
				String[] headerParts = h.split(HEADER_DELIMITER);
				result.put(headerParts[HEADER_KEY_INDEX], parseHeaderValue(h, headerParts[HEADER_VALUE_INDEX]));
			});

		return result;
	}

	private static List<String> parseHeaderValue(String header, String headerPart) {
		if (header.startsWith("Cookie: ")) {
			return List.of(headerPart);
		}
		return Arrays.stream(headerPart.split(HEADER_VALUE_DELIMITER))
			.map(String::strip)
			.toList();
	}

	private static HttpCookie initCookie(List<String> headers) {
		String cookieHeader = getCookieHeader(headers);
		if (cookieHeader == null) {
			return null;
		}
		String cookieValues = cookieHeader.split(HEADER_DELIMITER)[HEADER_VALUE_INDEX];
		return HttpCookie.from(cookieValues);
	}

	private static String getCookieHeader(List<String> headers) {
		if (headers == null || headers.isEmpty()) {
			return null;
		}

		return headers.stream()
			.filter(header -> header.startsWith("Cookie"))
			.findFirst()
			.orElse(null);
	}

	public HttpCookie getHttpCookie() {
		return httpCookie;
	}
}
