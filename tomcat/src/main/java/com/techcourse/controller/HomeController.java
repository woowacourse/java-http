package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Status;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        responseBuilder.status(Status.OK)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .body("Hello world!".getBytes());
    }
}
