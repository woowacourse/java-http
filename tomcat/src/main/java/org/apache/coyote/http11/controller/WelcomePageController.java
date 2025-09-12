package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request_response.HttpMethod;
import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;

public class WelcomePageController implements Controller {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals(HttpMethod.GET) && request.getUriPath().equals("/");
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        String responseBody = "Hello world!";
        return HttpResponse.builder()
            .status(HttpStatus.OK)
            .body(responseBody)
            .contentType("text/html;charset=utf-8")
            .build();
    }
}
