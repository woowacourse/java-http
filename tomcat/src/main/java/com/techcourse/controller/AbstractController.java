package com.techcourse.controller;

import java.util.List;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (!allowedMethods().contains(request.method())) {
            methodNotAllowed(response);
        } else if ("POST".equals(request.method())) {
            doPost(request, response);
        } else if ("GET".equals(request.method())) {
            doGet(request, response);
        } else {
            methodNotAllowed(response);
        }
    }

    protected abstract List<String> allowedMethods();

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    private void methodNotAllowed(HttpResponse response) {
        response.copyFrom(HttpResponse.methodNotAllowed(allowedMethods()));
    }
}
