package org.apache.catalina;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public class HandlerMapping {

	private final Map<String, RequestHandler> handlers;
	private final RequestHandler defaultHandler;

	public HandlerMapping(final Set<RequestHandler> handlers, final RequestHandler defaultHandler) {
		this.handlers = handlers.stream()
			.collect(Collectors.toMap(
				RequestHandler::getRequestPath,
				handler -> handler
			));
		this.defaultHandler = defaultHandler;
	}

	public Response handle(final Request request) {
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
