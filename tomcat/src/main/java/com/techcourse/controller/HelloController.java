package com.techcourse.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class HelloController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        final var responseBody = "Hello world!";
        response.send(HttpStatus.OK, "text/html;charset=utf-8", responseBody.getBytes(StandardCharsets.UTF_8));
    }
}
