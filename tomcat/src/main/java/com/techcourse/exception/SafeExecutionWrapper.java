package com.techcourse.exception;

import java.util.function.BiConsumer;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class SafeExecutionWrapper {

    private static final ExceptionHandler EXCEPTION_HANDLER = new ExceptionHandler();

    public static BiConsumer<HttpRequest, HttpResponse> withExceptionHandling(BiConsumer<HttpRequest, HttpResponse> execution) {
        return (request, response) -> {
            try {
                execution.accept(request, response);
            } catch (Exception e) {
                EXCEPTION_HANDLER.handle(e, response);
            }
        };
    }
}
