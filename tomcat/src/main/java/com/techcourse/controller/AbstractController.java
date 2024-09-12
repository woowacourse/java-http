package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse.Builder responseBuilder) {
        if (request.isMethod(Method.GET)) {
            doGet(request, responseBuilder);
        }
        if (request.isMethod(Method.POST)) {
            doPost(request, responseBuilder);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse.Builder responseBuilder) { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) { /* NOOP */ }
}
