package org.apache.coyote.http11.exception;

import java.util.List;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.exception.UncheckedServletException;

public class ExceptionHandlerManager {

    private final List<ExceptionHandler> exceptionHandlers = List.of(
        new UnauthorizedExceptionHandler(),
        new IllegalArgumentExceptionHandler()
    );

    public void handle(HttpRequest request, HttpResponse response, Exception e) {
        for (var exceptionHandler : exceptionHandlers) {
            if (exceptionHandler.support(e)) {
                exceptionHandler.handle(request, response, e);
                return;
            }
        }
        throw new UncheckedServletException(e);
    }
}
