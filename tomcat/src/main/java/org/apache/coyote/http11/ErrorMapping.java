package org.apache.coyote.http11;

import java.util.stream.Stream;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.handler.ExceptionHandler;
import nextstep.jwp.exception.handler.LoginFailedHandler;
import nextstep.jwp.exception.handler.MethodNotAllowedHandler;
import nextstep.jwp.exception.handler.NotFoundHandler;
import nextstep.jwp.exception.handler.RuntimeExceptionHandler;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.exception.ResourceNotFoundException;

public enum ErrorMapping {

    NOT_FOUND(new ResourceNotFoundException(), new NotFoundHandler()),
    LOGIN_FAILED(new LoginFailedException(), new LoginFailedHandler()),
    METHOD_NOT_ALLOWED(new MethodNotAllowedException(), new MethodNotAllowedHandler()),
    RUNTIME_EXCEPTION(new RuntimeException(), new RuntimeExceptionHandler()),
    ;

    private final Exception exception;
    private final ExceptionHandler handler;

    ErrorMapping(Exception exception, ExceptionHandler handler) {
        this.exception = exception;
        this.handler = handler;
    }

    public static ExceptionHandler findErrorHandler(Exception exception){
        final ErrorMapping errorMapping = Stream.of(values())
                .filter(it -> it.exception.getClass() == exception.getClass())
                .findAny()
                .orElse(RUNTIME_EXCEPTION);

        return errorMapping.handler;
    }
}
