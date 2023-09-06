package org.apache.coyote.http11.headers;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.apache.coyote.http11.headers.HttpHeaderType.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

	private static final String HEADER_DELIMITER = ":";
	private static final String LINE_SEPARATOR = "\r\n";

	private final Map<String, String> headers;
	private final HttpCookie cookie;

	public HttpHeaders() {
		headers = new LinkedHashMap<>();
		cookie = new HttpCookie();
	}

	public HttpHeaders(final Map<String, String> headers, final HttpCookie cookie) {
		this.headers = headers;
		this.cookie = cookie;
	}

	public static HttpHeaders from(final String httpRequest) {
		final Map<String, String> headerMaps = resolveHeaderMap(httpRequest);
		return Optional.ofNullable(headerMaps.get(COOKIE.getValue()))
			.map(cookie -> {
				//추후 리팩터링 고민하기
				headerMaps.remove(COOKIE.getValue());
				return new HttpHeaders(headerMaps, HttpCookie.from(cookie));
			})
			.orElseGet(() -> new HttpHeaders(headerMaps, new HttpCookie()));
	}

	private static Map<String, String> resolveHeaderMap(String httpRequest) {
		return stream(httpRequest.split(LINE_SEPARATOR))
			.map(headerLine -> headerLine.split(HEADER_DELIMITER, 2))
			.collect(toMap(
				header -> header[0].trim(),
				header -> header[1].trim()
			));
	}

	public static HttpHeaders of(final String body, final MimeType mimeType) {
		final Map<String, String> headers = new LinkedHashMap<>();
		headers.put(CONTENT_TYPE.getValue(), mimeType.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return new HttpHeaders(headers, new HttpCookie());
	}

	public String build() {
		return headers.entrySet().stream()
			.map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
			.collect(joining(LINE_SEPARATOR))
			+ LINE_SEPARATOR;
	}

	public void addLocation(final String location) {
		headers.put(LOCATION.getValue(), location);
	}

	public void addSetCookie(final String setCookieValue) {
		headers.put(SET_COOKIE.getValue(), setCookieValue);
	}

	public Optional<String> get(final String key) {
		return Optional.ofNullable(headers.get(key));
	}

	public boolean isExistJSessionId() {
		return cookie.isExistJSessionId();
	}

	public Optional<String> findJSessionId() {
		return cookie.getJSessionId();
	}
}
