package com.techcourse.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HelloController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        final var responseBody = "Hello world!";
        response.sendOk("text/html;charset=utf-8", responseBody.getBytes(StandardCharsets.UTF_8));
    }
}
