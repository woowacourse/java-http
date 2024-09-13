package com.techcourse.controller;

import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class WelcomePageController extends AbstractController {

    public WelcomePageController() {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setBody("Hello world!");
    }
}
