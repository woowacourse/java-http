package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.matchesMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.matchesMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
        response.setHttpStatus(HttpStatus.FOUND);
        response.sendRedirect("/404.html");
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}
