package org.apache.coyote.http11.handler;

import java.util.Objects;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class StaticContentRequestHandler implements RequestHandler {

	@Override
	public boolean canHandle(Request request) {
		return true;
	}

	@Override
	public Response handle(final Request request) {
		final String requestPath = request.getPath();
		Objects.requireNonNull(requestPath, "요청 경로는 null일 수 없습니다.");
		final var responseBody = ResourceProvider.provide(requestPath);
		final var mimeType = MimeType.fromPath(requestPath);
		return Response.ok(responseBody, mimeType);
	}
}
