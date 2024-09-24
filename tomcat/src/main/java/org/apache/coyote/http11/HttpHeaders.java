package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

	private static final String HEADER_DELIMITER = ":";
	private static final int SPLIT_LIMIT = 2;
	private static final int HEADER_NAME_INDEX = 0;
	private static final int HEADER_VALUE_INDEX = 1;
	private static final String CONTENT_LENGTH = "content-length";
	private static final String COOKIE = "cookie";
	private static final int DEFAULT_CONTENT_LENGTH = 0;

	private final Map<String, String> headers;

	public static HttpHeaders from(List<String> headerLines) {
		Map<String, String> headers = new HashMap<>();
		headerLines.stream()
			.map(headerLine -> headerLine.split(HEADER_DELIMITER, SPLIT_LIMIT))
			.filter(headerToken -> headerToken.length == SPLIT_LIMIT)
			.forEach(headerToken -> {
				String headerName = headerToken[HEADER_NAME_INDEX].trim().toLowerCase();
				String value = headerToken[HEADER_VALUE_INDEX].trim();
				headers.put(headerName, value);
			});
		return new HttpHeaders(headers);
	}

	public HttpHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void addHeader(String header, String value) {
		headers.put(header, value);
	}

	public List<String> getHeaderValues(String header) {
		String headerValue = headers.get(header);
		String valuesLine = headerValue.split(";")[0].trim();
		return Arrays.stream(valuesLine.split(","))
			.map(String::trim).toList();
	}

	public Optional<String> getHeader(String header) {
		String value = headers.get(header);
		if (value == null || value.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(value);
	}

	public int getContentLength() {
		return getHeader(CONTENT_LENGTH)
			.map(Integer::parseInt)
			.orElse(DEFAULT_CONTENT_LENGTH);
	}

	public Optional<String> getCookie(String cookieName) {
		return getHeader(COOKIE)
			.flatMap(cookieHeader -> Arrays.stream(cookieHeader.split(";"))
				.map(String::trim)
				.filter(cookie -> cookie.startsWith(cookieName + "="))
				.map(cookie -> cookie.substring(cookieName.length() + 1))
				.findFirst());
	}

	public boolean containsHeader(String header) {
		return headers.containsKey(header);
	}
}
