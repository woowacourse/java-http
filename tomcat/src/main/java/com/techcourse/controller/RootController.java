package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {
    private static final String HELLO_WORLD_BODY = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setContentType(ContentType.TEXT);
        response.setBody(HELLO_WORLD_BODY);
    }
}
