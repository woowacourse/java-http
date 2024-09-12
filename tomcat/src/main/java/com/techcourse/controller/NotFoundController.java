package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setLocation("404.html");
    }
}
