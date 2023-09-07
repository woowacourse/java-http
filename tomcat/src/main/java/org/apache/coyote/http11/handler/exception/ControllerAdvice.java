package org.apache.coyote.http11.handler.exception;

import java.io.IOException;

import org.apache.coyote.http11.response.HttpResponse;

public interface ControllerAdvice {

	boolean isSupported(final Exception exception);

	HttpResponse handleTo(final Exception exception) throws IOException;
}
