package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class HandlerMapping {

	private static final Map<String, RequestHandler> handlers;
	private static final RequestHandler defaultHandler;

	static {
		handlers = Stream.of(
			new BaseRequestHandler(),
			new LoginRequestHandler(),
			new RegisterRequestHandler()
		).collect(Collectors.toMap(
			RequestHandler::getRequestPath,
			handler -> handler
		));
		defaultHandler = new StaticContentRequestHandler();
	}

	private HandlerMapping() {
	}

	public static Response handle(final Request request) {
		final String requestPath = request.getPath();
		if (requestPath == null) {
			return Response.notFound();
		}
		final var handler = handlers.get(requestPath);
		if (handler == null) {
			return defaultHandler.handle(request);
		}
		return handler.handle(request);
	}
}
