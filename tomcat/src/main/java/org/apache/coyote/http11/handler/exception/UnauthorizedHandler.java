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
	public HttpResponse handleTo(final Exception e) throws IOException {
		return HttpResponse.redirect(REDIRECT_URI);
	}
}
