package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

public class InternalServerErrorController extends AbstractController {

    private static final String INTERNAL_SERVER_ERROR_LOCATION = "/500.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        generateStaticResponse(INTERNAL_SERVER_ERROR_LOCATION, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }
}
