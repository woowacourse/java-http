package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class AbstractController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();

        if (method.equals("POST")) {
            doPost(request, response);
        }

        if (method.equals("GET")) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}

    protected void doGet(HttpRequest request, HttpResponse response) {}
}
