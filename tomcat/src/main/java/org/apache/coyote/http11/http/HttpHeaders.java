package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class HttpHeaders {

	private static final String HEADER_DELIMITER = ": ";
	private static final int HEADER_KEY_INDEX = 0;
	private static final int HEADER_VALUE_INDEX = 1;

	private final Map<HttpHeader, String> headers = new LinkedHashMap<>();

	public HttpHeaders(Map<HttpHeader, String> headers) {
		this.headers.putAll(headers);
	}

	public HttpHeaders(BufferedReader requestReader) throws IOException {
		String line;
		while (!(line = requestReader.readLine()).isBlank()) {
			String[] header = line.split(HEADER_DELIMITER);
			injectHeaders(header[HEADER_KEY_INDEX], header[HEADER_VALUE_INDEX]);
		}
	}

	private void injectHeaders(String key, String value) {
		HttpHeader.from(key)
			.ifPresent(header -> this.headers.put(header, value));
	}

	public void addHeader(HttpHeader httpHeader, String value) {
		this.headers.put(httpHeader, value);
	}

	public List<HttpHeader> getHeaders() {
		return new ArrayList<>(headers.keySet());
	}

	public String getValue(HttpHeader header) {
		return findValue(header)
			.orElseThrow(() -> new NoSuchElementException("해당 헤더가 존재하지 않습니다."));
	}

	public Optional<String> findValue(HttpHeader header) {
		return Optional.ofNullable(headers.get(header));
	}

	public Cookie getCookie() {
		return Optional.ofNullable(headers.get(HttpHeader.COOKIE))
			.map(Cookie::new)
			.orElse(new Cookie());
	}

	@Override
	public String toString() {
		return "HttpHeaders{" +
			"headers=" + headers +
			'}';
	}
}
