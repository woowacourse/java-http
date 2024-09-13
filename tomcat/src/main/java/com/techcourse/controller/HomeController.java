package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        responseBuilder.status(Status.OK)
                .contentType("text/html")
                .body("Hello world!".getBytes());
    }
}
