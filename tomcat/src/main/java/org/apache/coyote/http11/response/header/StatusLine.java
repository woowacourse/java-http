package org.apache.coyote.http11.response.header;

import org.apache.coyote.http11.common.HttpMessageConfig;

public class StatusLine {

	private final String httpVersion;
	private final StatusCode statusCode;

	public StatusLine(final String httpVersion, final StatusCode statusCode) {
		this.httpVersion = httpVersion;
		this.statusCode = statusCode;
	}

	public String toMessage() {
		final StringBuilder message = new StringBuilder().append(httpVersion)
			.append(HttpMessageConfig.WORD_DELIMITER.getValue())
			.append(statusCode.toMessage())
			.append(HttpMessageConfig.WORD_DELIMITER.getValue());
		return new String(message);
	}
}
