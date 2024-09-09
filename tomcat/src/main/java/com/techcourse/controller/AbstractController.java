package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.matchesMethod("GET")) {
            doGet(request, response);
        }
        if (request.matchesMethod("POST")) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        /* NOOP */
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        /* NOOP */
    }
}
