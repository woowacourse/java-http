package org.apache.coyote.http11.handler;

import java.util.Objects;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticContentRequestHandler implements RequestHandler {

	@Override
	public boolean canHandle(HttpRequest request) {
		return true;
	}

	@Override
	public HttpResponse handle(final HttpRequest request) {
		final String requestPath = request.getPath();
		Objects.requireNonNull(requestPath, "요청 경로는 null일 수 없습니다.");
		final var responseBody = ResourceProvider.provide(requestPath);
		final var mimeType = MimeType.fromPath(requestPath);
		return HttpResponse.ok(responseBody, mimeType);
	}
}
