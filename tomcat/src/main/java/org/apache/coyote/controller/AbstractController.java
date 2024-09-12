package org.apache.coyote.controller;

import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("HttpMethod not found");
    }

    protected void doPost(HttpRequest request, HttpResponse response) { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) { /* NOOP */ }
}

