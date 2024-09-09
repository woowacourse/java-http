package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public class NotFoundController extends AbstractController {

    private static final String NOT_FOUND_LOCATION = "/404.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return generateResponse(STATIC_RESOURCE_LOCATION + NOT_FOUND_LOCATION, HttpStatus.NOT_FOUND);
    }
}
