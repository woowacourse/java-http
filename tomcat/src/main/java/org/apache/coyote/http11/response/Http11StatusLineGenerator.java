package org.apache.coyote.http11.response;

import org.apache.coyote.response.StatusLine;

public class Http11StatusLineGenerator {

	private static final String STATUS_LINE_DELIMITER = " ";
	private static final String LINE_END = " ";

	private Http11StatusLineGenerator() {
	}

	public static String generate(final StatusLine statusLine) {
		final var version = statusLine.getVersion().getVersion();
		final var code = statusLine.getCode();
		return String.join(STATUS_LINE_DELIMITER, version, Integer.toString(code.getCode()), code.getMessage())
			+ LINE_END;
	}
}
