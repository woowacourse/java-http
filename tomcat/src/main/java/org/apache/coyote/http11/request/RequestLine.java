package org.apache.coyote.http11.request;

public class RequestLine {

	private static final String MESSAGE_DELIMITER = " ";
	private static final int METHOD = 0;
	private static final int RESOURCE = 1;
	private static final int HTTP_VERSION = 2;

	private final String method;
	private final String resource;
	private final String httpVersion;

	private RequestLine(final String method, final String resource, final String httpVersion) {
		this.method = method;
		this.resource = resource;
		this.httpVersion = httpVersion;
	}

	public static RequestLine from(final String requestLine) {
		final String[] requestLineElement = requestLine.split(MESSAGE_DELIMITER);

		return new RequestLine(
			requestLineElement[METHOD], requestLineElement[RESOURCE], requestLineElement[HTTP_VERSION]
		);
	}
}
