package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpProtocolVersion;

public class HttpStatusLine {

	private static final String DELIMITER = " ";
	private static final String LINE_END = " ";

	private final HttpProtocolVersion version;
	private final StatusCode code;

	public HttpStatusLine(final StatusCode code) {
		this.version = HttpProtocolVersion.HTTP11;
		this.code = code;
	}

	public String formatStatusLine() {
		return String.join(DELIMITER, version.getVersion(), Integer.toString(code.getCode()), code.getMessage())
			+ LINE_END;
	}
}
