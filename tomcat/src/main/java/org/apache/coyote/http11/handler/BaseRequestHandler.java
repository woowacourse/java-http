package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class BaseRequestHandler implements RequestHandler {

	private static final String REQUEST_PATH = "/";
	private static final String RESPONSE_BODY = "Hello world!";

	@Override
	public boolean canHandle(final Request request) {
		return request.hasPath(REQUEST_PATH);
	}

	@Override
	public Response handle(final Request request) {
		return Response.ok(RESPONSE_BODY, MimeType.HTML);
	}
}
