package org.apache.catalina.controller;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String BAD_REQUEST_PAGE = "/400.html";
    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        try {
            routing(request, response);
        } catch (final IllegalArgumentException e) {
            response.setHttpStatus(HttpStatus.BAD_REQUEST).sendRedirect(BAD_REQUEST_PAGE);
        }
    }

    private void routing(final HttpRequest request, final HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        response.setHttpStatus(HttpStatus.NOT_FOUND).sendRedirect(NOT_FOUND_PAGE);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
