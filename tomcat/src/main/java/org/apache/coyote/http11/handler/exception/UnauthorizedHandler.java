package org.apache.coyote.http11.handler.exception;

import java.io.IOException;

import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.response.HttpResponse;

public class UnauthorizedHandler implements ControllerAdvice {

	private static final String REDIRECT_URI = "/401.html";

	@Override
	public boolean isSupported(final Exception exception) {
		return exception instanceof UnauthorizedException;
	}

	@Override
	public void handleTo(final Exception e, final HttpResponse response) throws IOException {
		response.redirect(REDIRECT_URI);
	}
}
