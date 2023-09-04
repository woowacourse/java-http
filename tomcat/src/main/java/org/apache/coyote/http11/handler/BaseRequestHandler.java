package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class BaseRequestHandler extends RequestHandler {

	private static final String REQUEST_PATH = "/";
	private static final String RESPONSE_BODY = "Hello world!";

	public BaseRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	protected Response doGet(final Request request) {
		return Response.ok(RESPONSE_BODY, MimeType.HTML);
	}
}
