package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        ok(response, "Hello world!");
    }
}
