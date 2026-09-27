package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMethod(GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethod(POST)) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
