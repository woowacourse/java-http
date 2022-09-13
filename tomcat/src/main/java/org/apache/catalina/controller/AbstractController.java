package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            doGet(request, response);
        }

        if (request.getMethod().equalsIgnoreCase("POST")) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
