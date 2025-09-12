package com.techcourse.controller;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final var method = request.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
