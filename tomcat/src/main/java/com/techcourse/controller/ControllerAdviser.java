package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.exception.client.BadRequestException;
import com.techcourse.exception.client.NotFoundException;
import com.techcourse.exception.client.UnauthorizedException;
import com.techcourse.exception.server.InternalServerException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class ControllerAdviser {

    private static final Map<Class<? extends UncheckedServletException>, Consumer<HttpResponse>> exceptionStatusMap = new ConcurrentHashMap<>();

    static {
        exceptionStatusMap.put(BadRequestException.class,
                (response) -> response.setStatus(HttpStatus.BAD_REQUEST));
        exceptionStatusMap.put(UnauthorizedException.class,
                (response -> setErrorResponse(response, HttpStatus.UNAUTHORIZED, "401.html")));
        exceptionStatusMap.put(NotFoundException.class,
                (response -> setErrorResponse(response, HttpStatus.NOT_FOUND, "404.html")));
        exceptionStatusMap.put(InternalServerException.class,
                (response -> setErrorResponse(response, HttpStatus.INTERNAL_SERVER_EXCEPTION, "500.html")));
    }

    public static void service(UncheckedServletException exception, HttpRequest request, HttpResponse response) {
        Consumer<HttpResponse> handler = exceptionStatusMap.get(exception.getClass());
        if (handler == null) {
            handler = (httpResponse) ->
                    setErrorResponse(httpResponse, HttpStatus.INTERNAL_SERVER_EXCEPTION, "500.html");
        }
        handler.accept(response);
        response.setHeaders(HttpHeaders.create(request, response));
    }

    private static void setErrorResponse(HttpResponse response, HttpStatus httpStatus, String viewName) {
        response.setStatus(httpStatus);
        response.setView(ViewResolver.getView(viewName));
    }
}
