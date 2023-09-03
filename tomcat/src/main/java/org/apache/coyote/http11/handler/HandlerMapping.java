package org.apache.coyote.http11.handler;

import java.util.List;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

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

	public static HttpResponse handle(final HttpRequest request) {
		for (final RequestHandler handler : handlers) {
			if (handler.canHandle(request)) {
				return handler.handle(request);
			}
		}
		return defaultHandler.handle(request);
	}
}
