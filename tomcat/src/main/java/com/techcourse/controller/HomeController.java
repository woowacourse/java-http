package com.techcourse.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String CONTENT_TYPE = "text/html";
    private static final String BODY = "Hello world!";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setBody(CONTENT_TYPE, BODY);
    }
}