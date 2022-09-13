package org.apache.coyote;

import org.apache.coyote.http11.request.Request;

public interface Container {

    Controller findController(final Request request);

    ExceptionHandler findExceptionHandler(final Exception exception);
}
