package com.techcourse.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.StatusCode;

public class NotFoundController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception {
        buildRedirectResponse("/401.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        throw new RuntimeException();
    }

    private void buildRedirectResponse(String location, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode(StatusCode.FOUND)
                .addHeader("Location", location);
    }
}
