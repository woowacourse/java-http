package com.techcourse.handler;

import hoony.was.RequestHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;

public class LoginGetRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.hasMethod(HttpMethod.GET) && request.hasPath("/login");
    }

    @Override
    public String handle(HttpRequest request) {
        return "login.html";
    }
}
