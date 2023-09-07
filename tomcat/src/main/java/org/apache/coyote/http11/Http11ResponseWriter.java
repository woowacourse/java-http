package org.apache.coyote.http11;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.apache.coyote.Cookie;
import org.apache.coyote.response.HeaderType;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseHeader;
import org.apache.coyote.response.StatusLine;

public class Http11ResponseWriter {

	private static final String CRLF = "\r\n";
	private static final String STATUS_LINE_DELIMITER = " ";
	private static final String LINE_END = " ";
	private static final String HEADER_SEPARATOR = ": ";
	private static final String HEADER_BODY_SEPARATOR = "";
	private static final String COOKIE_DELIMITER = "; ";
	private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";

	private final BufferedWriter writer;

	public Http11ResponseWriter(final BufferedWriter writer) {
		this.writer = writer;
	}

	public void write(final Response response) throws IOException {
		final var statusLine = response.getStatusLine();
		final var header = response.getHeader();
		final var responseBody = response.getResponseBody();

		writer.write(String.join(CRLF,
			format(statusLine),
			format(header),
			HEADER_BODY_SEPARATOR,
			responseBody));

		writer.flush();
	}

	private String format(final StatusLine statusLine) {
		final var version = statusLine.getVersion().getVersion();
		final var code = statusLine.getCode();
		return String.join(STATUS_LINE_DELIMITER, version, Integer.toString(code.getCode()), code.getMessage())
			+ LINE_END;
	}

	private String format(final ResponseHeader responseHeader) {
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

	public String format(final List<Cookie> cookies) {
		return cookies.stream()
			.map(cookie -> cookie.getKey() + COOKIE_KEY_VALUE_SEPARATOR + cookie.getValue() + COOKIE_DELIMITER)
			.collect(joining());
	}
}
