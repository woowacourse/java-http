package com.techcourse.controller;

import jakarta.controller.AbstractController;
import jakarta.http.ContentType;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.HTML);
        response.setResponseBody("Hello world!".getBytes());
    }
}
