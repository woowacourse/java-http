package org.apache;

import java.util.*;

public class HttpHeaders {

	public static final String HEADER_DELIMITER = ":";

	private final Map<HttpHeader, List<String>> headers;

	public static HttpHeaders from(List<String> headerLines) {
		Map<HttpHeader, List<String>> headers = new HashMap<>();
		headerLines.stream()
			.map(headerLine -> headerLine.split(HEADER_DELIMITER, 2))
			.filter(headerToken -> headerToken.length == 2)
			.forEach(headerToken -> {
				HttpHeader header = HttpHeader.from(headerToken[0].trim());
				String value = headerToken[1].trim();
				List<String> headerValues = Arrays.stream(value.split(",")).map(String::trim).toList();
				headers.computeIfAbsent(header, key -> new ArrayList<>()).addAll(headerValues);
			});
		return new HttpHeaders(headers);
	}

	public HttpHeaders(Map<HttpHeader, List<String>> headers) {
		this.headers = headers;
	}

	public void addHeader(HttpHeader header, String value) {
		headers.computeIfAbsent(header, key -> new ArrayList<>()).add(value);
	}

	public List<String> getHeaderValues(HttpHeader header) {
		return headers.getOrDefault(header, Collections.emptyList());
	}

	public Optional<String> getHeader(HttpHeader header) {
		List<String> values = headers.get(header);
		if (values == null || values.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(values.get(0));
	}

	public OptionalLong getContentLength() {
		return getHeader(HttpHeader.CONTENT_LENGTH)
			.map(Long::parseLong)
			.map(OptionalLong::of)
			.orElse(OptionalLong.empty());
	}

	public Optional<String> getCookie(String cookieName) {
		return getHeader(HttpHeader.COOKIE)
			.flatMap(cookieHeader -> Arrays.stream(cookieHeader.split(";"))
				.map(String::trim)
				.filter(cookie -> cookie.startsWith(cookieName + "="))
				.map(cookie -> cookie.substring(cookieName.length() + 1))
				.findFirst());
	}

	public boolean containsHeader(HttpHeader header) {
		return headers.containsKey(header);
	}
}
