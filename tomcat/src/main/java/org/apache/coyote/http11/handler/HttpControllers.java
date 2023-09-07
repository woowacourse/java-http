package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.http11.handler.exception.ControllerAdvice;
import org.apache.coyote.http11.handler.exception.InternalServerErrorHandler;
import org.apache.coyote.http11.handler.exception.UnauthorizedHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;

public class HttpControllers {

	private static final List<HttpController> HTTP_CONTROLLERS = List.of(
		new RootController(),
		new StaticResourceController(),
		new LoginController(),
		new RegisterController()
	);
	private static final List<ControllerAdvice> EXCEPTION_HANDLERS = List.of(
		new UnauthorizedHandler()
	);

	public void handleTo(final HttpRequest request, final HttpResponse response) throws IOException {
		try {
			HTTP_CONTROLLERS.stream()
				.filter(handler -> handler.isSupported(request))
				.findAny()
				.orElseGet(NotFoundController::new)
				.handleTo(request, response);
		} catch (final Exception e) {
			EXCEPTION_HANDLERS.stream()
				.filter(handler -> handler.isSupported(e))
				.findAny()
				.orElseGet(InternalServerErrorHandler::new)
				.handleTo(e, response);
		}
	}
}
