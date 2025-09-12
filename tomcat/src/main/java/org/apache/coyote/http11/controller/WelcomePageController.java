package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;

public class WelcomePageController extends ServletController {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getUriPath().equals("/");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        String responseBody = "Hello world!";
        return HttpResponse.builder()
            .status(HttpStatus.OK)
            .body(responseBody)
            .contentType("text/html;charset=utf-8")
            .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        return null;
    }
}
