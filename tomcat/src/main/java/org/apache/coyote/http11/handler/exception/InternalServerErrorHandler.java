package org.apache.coyote.http11.handler.exception;

import java.io.IOException;

import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalServerErrorHandler implements ControllerAdvice {

	private static final Logger log = LoggerFactory.getLogger(InternalServerErrorHandler.class);
	private static final String REDIRECT_URI = "/500.html";

	@Override
	public boolean isSupported(final Exception exception) {
		return false;
	}

	@Override
	public void handleTo(final Exception e, final HttpResponse response) throws IOException {
		log.error(e.getMessage(), e);
		response.redirect(REDIRECT_URI);
	}
}
