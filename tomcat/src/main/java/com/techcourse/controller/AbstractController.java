package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getRequestLine().getMethod();
        if (method.equals("POST")) {
            doPost(request, response);
        } else if (method.equals("GET")) {
            doGet(request, response);
        } else {
            response.setStatus405();
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }
}
