package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpMessageGenerator;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

public class NotFoundController extends AbstractController {

    private static final String NOT_FOUND_LOCATION = "/404.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpMessageGenerator.generateStaticResponse(NOT_FOUND_LOCATION, HttpStatus.NOT_FOUND, response);
    }
}
