package com.techcourse.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
            return;
        }
        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
