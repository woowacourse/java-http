package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(DEFAULT_MESSAGE.getBytes(StandardCharsets.UTF_8));
    }
}
