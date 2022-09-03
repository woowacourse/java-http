package org.apache.coyote.http11.request;

public class RequestLine {

	private static final String MESSAGE_DELIMITER = " ";
	private static final int METHOD = 0;
	private static final int RESOURCE = 1;
	private static final int HTTP_VERSION = 2;

	private final Method method;
	private final Resource resource;
	private final String httpVersion;

	private RequestLine(final Method method, final Resource resource, final String httpVersion) {
		this.method = method;
		this.resource = resource;
		this.httpVersion = httpVersion;
	}

	public static RequestLine from(final String requestLine) {
		final String[] requestLineElement = requestLine.split(MESSAGE_DELIMITER);

		return new RequestLine(
			Method.findBy(requestLineElement[METHOD]),
			Resource.from(requestLineElement[RESOURCE]),
			requestLineElement[HTTP_VERSION]
		);
	}
}
