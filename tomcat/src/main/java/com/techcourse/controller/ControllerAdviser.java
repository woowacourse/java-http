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
            response.setView(ViewResolver.getView("400.html"));
        }
        if (exception instanceof UnauthorizedException) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setView(ViewResolver.getView("401.html"));
        }
        if (exception instanceof NotFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setView(ViewResolver.getView("404.html"));
        }
        if (exception instanceof InternalServerException) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_EXCEPTION);
            response.setView(ViewResolver.getView("500.html"));
        }

        response.setHeaders(HttpHeaders.create(request, response));
    }
}
