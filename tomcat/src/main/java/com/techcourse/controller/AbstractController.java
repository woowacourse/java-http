package com.techcourse.controller;

import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        final String method = request.getRequestLine().getMethod();

        switch (method) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            default -> response.methodNotAllowed();
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response){}
    protected void doPost(HttpRequest request, HttpResponse response){}
}
