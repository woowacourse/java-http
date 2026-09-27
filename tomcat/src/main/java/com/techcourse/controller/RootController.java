package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RootController extends AbstractController {

    private static final String ROOT_RESPONSE_BODY = "Hello world!";

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) {
        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8 ");
        response.setBody(ROOT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8));
    }
}
