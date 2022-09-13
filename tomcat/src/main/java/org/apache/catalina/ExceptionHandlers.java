package org.apache.catalina;

import org.apache.coyote.ExceptionHandler;

public interface ExceptionHandlers {

    ExceptionHandler find(final Exception exception);
}
