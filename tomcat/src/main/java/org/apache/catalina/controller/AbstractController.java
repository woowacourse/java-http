package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.StatusLine;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isMethod(GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethod(POST)) {
            doPost(request, response);
            return;
        }
        doDefault(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) { /* NOOP */ }

    protected void doPost(HttpRequest request, HttpResponse response) { /* NOOP */ }
    private void doDefault(HttpRequest request, HttpResponse response) {
        response.statusLine(new StatusLine(request.getProtocolVersion(), HttpStatus.NOT_FOUND))
                .redirect("/404.html");
    }
}