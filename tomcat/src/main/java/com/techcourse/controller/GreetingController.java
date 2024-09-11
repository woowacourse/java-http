package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class GreetingController extends AbstractController {

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setBody("Hello world!");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException();
    }
}
