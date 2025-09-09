package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestMethod;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasMethod(HttpRequestMethod.GET)) {
            doGet(request, response);
        }

        if (request.hasMethod(HttpRequestMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
