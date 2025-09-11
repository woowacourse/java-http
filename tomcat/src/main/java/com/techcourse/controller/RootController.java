package com.techcourse.controller;

import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        byte[] body = "Hello world!".getBytes();
        return HttpResponse.ok()
                .contentType(ContentType.TEXT_PLAIN)
                .contentLength(body.length)
                .body(body)
                .build();
    }
}
