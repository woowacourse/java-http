package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.exception.client.BadRequestException;
import com.techcourse.exception.client.NotFoundException;
import com.techcourse.exception.client.UnauthorizedException;
import com.techcourse.exception.server.InternalServerException;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class ControllerAdviser {

    private ControllerAdviser() {}

    public static void service(UncheckedServletException exception, HttpRequest request, HttpResponse response) {
        if (exception instanceof BadRequestException) {
            response.setStatus(HttpStatus.BAD_REQUEST);
        }
        if (exception instanceof UnauthorizedException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "401.html");
            return;
        }
        if (exception instanceof NotFoundException) {
            setErrorResponse(response, HttpStatus.NOT_FOUND, "404.html");
            return;
        }
        if (exception instanceof InternalServerException) {
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_EXCEPTION, "500.html");
            return;
        }

        response.setHeaders(HttpHeaders.create(request, response));
    }

    private static void setErrorResponse(HttpResponse response, HttpStatus httpStatus, String viewName) {
        response.setStatus(httpStatus);
        response.setView(ViewResolver.getView(viewName));
    }
}
