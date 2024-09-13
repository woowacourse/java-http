package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.getPath().equals("/")) {
            response.addHeader("Content-Length", Integer.toString("Hello world!".length()));
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.setResponseBody("Hello world!");
        }
    }
}
