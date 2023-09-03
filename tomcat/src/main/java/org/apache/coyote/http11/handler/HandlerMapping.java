package org.apache.coyote.http11.handler;

import java.util.List;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class HandlerMapping {

	private static final List<RequestHandler> handlers;
	private static final RequestHandler defaultHandler;

	static {
		handlers = List.of(
			new BaseRequestHandler(),
			new LoginRequestHandler()
		);
		defaultHandler = new StaticContentRequestHandler();
	}

	private HandlerMapping() {
	}

	public static Response handle(final Request request) {
		try {
			for (final RequestHandler handler : handlers) {
				if (handler.canHandle(request)) {
					return handler.handle(request);
				}
			}
			return defaultHandler.handle(request);
		} catch (IllegalArgumentException e) {
			return Response.notFound();
		}
	}
}
