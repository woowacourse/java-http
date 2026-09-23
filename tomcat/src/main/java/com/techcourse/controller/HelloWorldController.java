package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public final class HelloWorldController extends AbstractController {

    public HelloWorldController() {
        super(HttpMethod.GET);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.OK);
        response.setContentType("text/html");
        response.setBody("Hello world!");
    }
}
