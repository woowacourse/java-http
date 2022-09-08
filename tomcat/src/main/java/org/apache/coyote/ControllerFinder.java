package org.apache.coyote;

import org.apache.coyote.http11.request.Request;

public interface ControllerFinder {

    Controller findController(final Request request);

    ExceptionController findExceptionHandler(final Exception exception);
}
