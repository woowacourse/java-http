package org.apache.catalina;

import org.apache.coyote.controller.Controller;

public interface ExceptionHandler {

    Controller getHandler(final Exception exception);
}
