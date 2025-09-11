package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import java.nio.charset.StandardCharsets;

public class GreetingController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        final var body = "Hello world!";

        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(200, "OK")
                .contentType("text/plain;charset=utf-8")
                .body(body.getBytes(StandardCharsets.UTF_8))
                .build();
    }
}
