package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);

        response.setStatusLine("HTTP/1.1", 200, "OK");
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(body);
    }
}
