package com.techcourse.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("POST".equals(request.method())) {
            doPost(request, response);
        }

        if ("GET".equals(request.method())) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
