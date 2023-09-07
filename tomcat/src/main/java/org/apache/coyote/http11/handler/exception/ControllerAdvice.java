package org.apache.coyote.http11.handler.exception;

import java.io.IOException;

import org.apache.coyote.http11.response.HttpResponse;

public interface ControllerAdvice {

	boolean isSupported(final Exception exception);

	void handleTo(final Exception exception, final HttpResponse response) throws IOException;
}
