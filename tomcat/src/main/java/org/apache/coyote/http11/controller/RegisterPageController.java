package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request_response.HttpMethod;
import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.util.StaticFileReader;
import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;

public class RegisterPageController implements Controller {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals(HttpMethod.GET) && request.getUriPath().equals("/register");
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        String responseBody = new StaticFileReader().readStaticFile("/register.html");
        return HttpResponse.builder()
            .status(HttpStatus.OK)
            .contentType("text/html;charset=utf-8")
            .body(responseBody)
            .build();
    }
}
