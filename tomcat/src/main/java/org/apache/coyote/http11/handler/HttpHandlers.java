package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.http11.handler.exception.ExceptionHandler;
import org.apache.coyote.http11.handler.exception.InternalServerErrorHandler;
import org.apache.coyote.http11.handler.exception.UnauthorizedHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpHandlers {

	private static final List<HttpHandler> HTTP_HANDLERS = List.of(
		new RootHandler(),
		new StaticResourceHandler(),
		new LoginHandler(),
		new RegisterHandler()
	);
	private static final List<ExceptionHandler> EXCEPTION_HANDLERS = List.of(
		new UnauthorizedHandler()
	);

	public HttpResponse handleTo(final HttpRequest request) throws IOException {
		try {
			return HTTP_HANDLERS.stream()
				.filter(handler -> handler.isSupported(request))
				.findAny()
				.orElseGet(NotFoundHandler::new)
				.handleTo(request);
		} catch (final Exception e) {
			return EXCEPTION_HANDLERS.stream()
				.filter(handler -> handler.isSupported(e))
				.findAny()
				.orElseGet(InternalServerErrorHandler::new)
				.handleTo(e);
		}
	}
}
