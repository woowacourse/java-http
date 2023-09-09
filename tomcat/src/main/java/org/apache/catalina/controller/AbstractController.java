package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.StatusLine;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getRequestLine().getHttpMethod().is(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.getRequestLine().getHttpMethod().is(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        response.statusLine(
                new StatusLine(request.getRequestLine().getVersion(), HttpStatus.NOT_FOUND)
        ).redirect("/404.html");
    }

    protected void doPost(HttpRequest request, HttpResponse response) { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response) { /* NOOP */ }
}