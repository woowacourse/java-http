package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class GreetingController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        final var body = "Hello world!".getBytes();

        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(200, "OK")
                .contentType("text/plain;charset=utf-8")
                .body(body)
                .build();
    }
}
