package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.nio.charset.StandardCharsets;

public class RootController extends AbstractController {
    private static final byte[] DEFAULT_BODY = "Hello world!".getBytes(StandardCharsets.UTF_8);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setBody(MimeType.TEXT_HTML, DEFAULT_BODY);
    }
}
