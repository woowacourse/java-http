package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HelloController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        final var responseBody = "Hello world!";
        response.sendOk("text/html;charset=utf-8", responseBody.getBytes(StandardCharsets.UTF_8));
    }
}
