package com.techcourse.handler;

import hoony.was.RequestHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterGetRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.hasMethod(HttpMethod.GET) && request.hasPath("/register");
    }

    @Override
    public String handle(HttpRequest request, HttpResponse response) {
        return "register.html";
    }
}
