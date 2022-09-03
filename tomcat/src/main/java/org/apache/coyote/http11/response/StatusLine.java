package org.apache.coyote.http11.response;

public class StatusLine {

	private final String httpVersion;
	private final StatusCode statusCode;

	private StatusLine(final String httpVersion, final StatusCode statusCode) {
		this.httpVersion = httpVersion;
		this.statusCode = statusCode;
	}

	public String toMessage() {
		return String.join(" ", httpVersion, statusCode.toMessage());
	}
}
