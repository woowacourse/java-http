package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.matchesMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.matchesMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        response.setHttpStatus(HttpStatus.FOUND);
        response.sendRedirect(NOT_FOUND_PAGE);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}
