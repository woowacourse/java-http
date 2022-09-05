package org.apache.coyote.http11.request;

import static nextstep.jwp.exception.ExceptionType.INVALID_REQUEST_LINE_EXCEPTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestHeaders {

	private static final int DEFAULT_HEADER_FIELD_LENGTH = 2;

	private final Map<String, String> headers;

	private RequestHeaders(final Map<String, String> headers) {
		this.headers = headers;
	}

	public static RequestHeaders from(final BufferedReader bufferedReader) {
		try {
			return new RequestHeaders(parseHeaders(bufferedReader));
		} catch (final IOException e) {
			throw new IllegalArgumentException(INVALID_REQUEST_LINE_EXCEPTION.getMessage());
		}
	}

	private static Map<String, String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
		final Map<String, String> headers = new HashMap<>();

		while (bufferedReader.ready()) {
			final String line = bufferedReader.readLine();
			Objects.requireNonNull(line);
			if ("".equals(line)) {
				break;
			}
			addHeader(headers, line);
		}
		return headers;
	}

	private static void addHeader(final Map<String, String> headers, final String line) {
		final String[] field = line.split(": ");
		validateFieldLength(field);
		headers.put(field[0], field[1]);
	}

	private static void validateFieldLength(final String[] field) {
		if (field.length != DEFAULT_HEADER_FIELD_LENGTH) {
			throw new IllegalArgumentException(INVALID_REQUEST_LINE_EXCEPTION.getMessage());
		}
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
