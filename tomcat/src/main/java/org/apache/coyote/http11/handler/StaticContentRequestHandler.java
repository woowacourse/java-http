package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class StaticContentRequestHandler extends RequestHandler {

	protected StaticContentRequestHandler() {
		super("/*");
	}

	@Override
	public Response handle(final Request request) {
		final String requestPath = request.getPath();
		if (requestPath == null) {
			return Response.notFound();
		}
		final var responseBody = ResourceProvider.provide(requestPath);
		final var mimeType = MimeType.fromPath(requestPath);
		return Response.ok(responseBody, mimeType);
	}
}
