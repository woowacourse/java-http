package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.util.List;

public abstract class AbstractController implements Controller {

    private final List<String> allowedMethods;

    protected AbstractController(final String... allowedMethods) {
        this.allowedMethods = List.of(allowedMethods);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (!allowedMethods.contains(request.getMethod())) {
            response.sendMethodNotAllowed(allowedMethods);
            return;
        }

        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
            return;
        }

        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
            return;
        }
        response.sendMethodNotAllowed(allowedMethods);
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.sendMethodNotAllowed(allowedMethods);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        response.sendMethodNotAllowed(allowedMethods);
    }
}
