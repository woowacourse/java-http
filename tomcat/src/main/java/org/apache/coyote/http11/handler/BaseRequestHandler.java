package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class BaseRequestHandler implements RequestHandler {

	private static final String REQUEST_PATH = "/";
	private static final String RESPONSE_BODY = "Hello world!";

	@Override
	public boolean canHandle(final HttpRequest request) {
		return request.hasPath(REQUEST_PATH);
	}

	@Override
	public HttpResponse handle(final HttpRequest request) {
		return HttpResponse.ok(RESPONSE_BODY, MimeType.HTML);
	}
}
