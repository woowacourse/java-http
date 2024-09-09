package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public class InternalServerErrorController extends AbstractController {

    private static final String INTERNAL_SERVER_ERROR_LOCATION = "/500.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return generateResponse(STATIC_RESOURCE_LOCATION + INTERNAL_SERVER_ERROR_LOCATION, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
