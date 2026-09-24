package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setStatus("200 OK");
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8));
        response.send();
    }
}
