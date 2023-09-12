package org.apache.catalina;

import static org.apache.coyote.response.StatusCode.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public class HandlerMapping implements Handler {

	private final Map<String, AbstractHandler> handlers;
	private final AbstractHandler defaultHandler;

	public HandlerMapping(final Set<AbstractHandler> handlers, final AbstractHandler defaultHandler) {
		this.handlers = handlers.stream()
			.collect(Collectors.toMap(
				AbstractHandler::getRequestPath,
				handler -> handler
			));
		this.defaultHandler = defaultHandler;
	}

	@Override
	public void service(final Request request, final Response response) {
		final String requestPath = request.getPath();
		if (requestPath == null) {
			response.redirect(NOT_FOUND.getResourcePath());
			return;
		}
		final var handler = handlers.get(requestPath);
		if (handler == null) {
			defaultHandler.service(request, response);
			return;
		}
		handler.service(request, response);
	}
}
