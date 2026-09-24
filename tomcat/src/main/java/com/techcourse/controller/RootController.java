package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.nio.charset.StandardCharsets;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        writeOk(response, body, "text/html;charset=utf-8");
    }
}
