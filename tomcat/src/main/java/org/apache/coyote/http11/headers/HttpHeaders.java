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
		final Map<String, String> headerMaps = stream(httpRequest.split(LINE_SEPARATOR))
			.skip(1)
			.map(headerLine -> headerLine.split(HEADER_DELIMITER, 2))
			.collect(toMap(
				header -> header[0].trim(),
				header -> header[1].trim()
			));
		return Optional.ofNullable(headerMaps.get(COOKIE.getValue()))
			.map(cookie -> {
				//추후 리팩터링 고민하기
				headerMaps.remove(COOKIE.getValue());
				return new HttpHeaders(headerMaps, HttpCookie.from(cookie));
			})
			.orElseGet(() -> new HttpHeaders(headerMaps, new HttpCookie()));
	}

	public String build() {
		return headers.entrySet().stream()
			.map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
			.collect(joining(LINE_SEPARATOR))
			+ LINE_SEPARATOR;
	}

	public void put(final String key, final String value) {
		if (key.equals(COOKIE.getValue())) {
			//추후 cookie를 추가하는 로직이 생기면 그때 처리
		}
		headers.put(key, value);
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
