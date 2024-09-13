package com.techcourse.controller.exception;

import org.apache.catalina.ExceptionHandler;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.exception.NotFoundException;
import org.apache.coyote.exception.UnauthorizedException;

public class ExceptionMapping implements ExceptionHandler {

    private static final ExceptionMapping INSTANCE = new ExceptionMapping();

    private ExceptionMapping() {
    }

    public static ExceptionMapping getInstance() {
        return INSTANCE;
    }

    public Controller getHandler(final Exception exception) {
        if (exception instanceof NotFoundException) {
            return NotFoundController.getInstance();
        }

        if (exception instanceof UnauthorizedException) {
            return UnAuthorizationController.getInstance();
        }

        return InternalServerErrorController.getInstance();
    }
}
