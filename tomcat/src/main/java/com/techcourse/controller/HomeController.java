package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HomeController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.HTML);
        response.setBody(DEFAULT_MESSAGE);
    }
}
