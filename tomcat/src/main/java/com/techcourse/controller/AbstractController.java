package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse.Builder responseBuilder) {
        if (request.method().equals("GET")) {
            doGet(request, responseBuilder);
        }
        if (request.method().equals("POST")) {
            doPost(request, responseBuilder);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse.Builder responseBuilder) { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) { /* NOOP */ }
}
