package com.techcourse.controller;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class RegisterController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isSameMethod(HttpMethod.GET)) {
            response.setView(ViewResolver.getView("register.html"));
            response.setStatus(HttpStatus.OK);
            response.setHeaders(HttpHeaders.of(request, response));
        }
    }
}
