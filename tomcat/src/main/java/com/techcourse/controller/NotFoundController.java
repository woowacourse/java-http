package com.techcourse.controller;

import jakarta.controller.AbstractController;
import jakarta.http.ContentType;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpStatus;

public class NotFoundController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.HTML);
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setResponseBody(readResource("404.html"));
    }
}
