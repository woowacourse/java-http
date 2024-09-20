package org.apache.catalina.controller;

import org.apache.catalina.http.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

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

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("HttpMethod unsupported");
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("HttpMethod unsupported");
    }
}

