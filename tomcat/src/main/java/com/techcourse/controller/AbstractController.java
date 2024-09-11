package com.techcourse.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.hasMethod(HttpMethod.POST)) {
            doPost(request, response);
        }

        if (request.hasMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
