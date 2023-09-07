package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticContentRequestHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(StaticContentRequestHandler.class);
	private static final String REQUEST_PATH = "/*";

	protected StaticContentRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	public Response handle(final Request request) {
		final String requestPath = request.getPath();
		if (requestPath == null) {
			return Response.notFound();
		}
		try {
			final var responseBody = ResourceProvider.provide(requestPath);
			final var mimeType = MimeType.fromPath(requestPath);
			return Response.ok(responseBody, mimeType);
		} catch (IllegalArgumentException e) {
			log.warn("{}: {}", request.getPath(), e.getMessage());
			return Response.notFound();
		}
	}
}
