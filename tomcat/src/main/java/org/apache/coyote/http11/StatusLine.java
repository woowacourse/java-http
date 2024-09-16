package org.apache.coyote.http11;

public class StatusLine {
	private static final String PROTOCOL_VERSION = "HTTP/1.1";

	private final int statusCode;
	private final String statusMessage;

	private StatusLine(int statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public static StatusLine from(HttpStatus httpStatus) {
		return new StatusLine(
			httpStatus.getCode(),
			httpStatus.getMessage()
		);
	}

	public String getLine() {
		return String.join(" ", PROTOCOL_VERSION, String.valueOf(statusCode), statusMessage);
	}
}
