package com.techcourse.exception.handler;

import java.util.List;
import org.apache.coyote.http11.response.HttpResponse;

public class GlobalExceptionHandler implements ExceptionHandler {

    private static final ExceptionHandler DEFAULT_HANDLER = new DefaultExceptionHandler();

    public boolean canHandle(Exception e) {
        return true;
    }

    List<ExceptionHandler> exceptionHandlers = List.of(
            new UnauthorizedExceptionHandler(),
            new IllegalArgumentExceptionHandler(),
            new DefaultExceptionHandler()
    );

    @Override
    public void handle(Exception e, HttpResponse response) {
        exceptionHandlers.stream()
                .filter(exceptionHandler -> exceptionHandler.canHandle(e))
                .findFirst()
                .orElse(DEFAULT_HANDLER)
                .handle(e, response);
    }
}
