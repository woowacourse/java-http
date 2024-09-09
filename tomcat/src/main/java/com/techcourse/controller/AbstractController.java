package com.techcourse.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception {
        String method = request.getMethod();
        if ("GET".equals(method)) {
            doGet(request, response);
        }

        if ("POST".equals(method)) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception;

    protected abstract void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response);
}
