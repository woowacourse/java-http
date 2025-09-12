package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isGetMethod()) {
            doGet(request, response);
            return;
        }
        if (request.isPostMethod()) {
            doPost(request, response);
            return;
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
