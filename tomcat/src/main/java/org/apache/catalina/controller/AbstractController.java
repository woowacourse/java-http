package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
            return;
        }

        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
            return;
        }

        response.methodNotAllowed();
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.methodNotAllowed();
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.methodNotAllowed();
    }
}
