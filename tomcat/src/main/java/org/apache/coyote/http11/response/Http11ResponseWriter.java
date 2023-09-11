package org.apache.coyote.http11.response;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.coyote.response.Response;

public class Http11ResponseWriter {

	private static final String CRLF = "\r\n";
	private static final String HEADER_BODY_SEPARATOR = "";

	private final BufferedWriter writer;

	public Http11ResponseWriter(final BufferedWriter writer) {
		this.writer = writer;
	}

	public void write(final Response response) throws IOException {
		if (!response.isCommitted()) {
			throw new IllegalArgumentException("올바르지 않은 Response 입니다.");
		}

		final var statusLine = response.getStatusLine();
		final var header = response.getHeader();
		final var responseBody = response.getResponseBody();

		writer.write(String.join(CRLF,
			Http11StatusLineGenerator.generate(statusLine),
			Http11ResponseHeaderGenerator.generate(header),
			HEADER_BODY_SEPARATOR,
			responseBody));

		writer.flush();
	}
}
