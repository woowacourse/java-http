package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
            return;
        }

        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
            return;
        }

        response.setStatus(405, "Method Not Allowed");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
