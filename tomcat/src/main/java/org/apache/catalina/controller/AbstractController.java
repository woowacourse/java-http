package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getMethod().equals("GET")) {
            get(request, response);
            return;
        }
        if (request.getMethod().equals("POST")) {
            post(request, response);
        }
    }

    protected void get(HttpRequest request, HttpResponse response) {
    }

    protected void post(HttpRequest request, HttpResponse response) {
    }
}
