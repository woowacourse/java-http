package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {
    private final Map<Request, Controller> requestMap = new HashMap<>();

    public RequestMapping() {
        requestMap.put(Request.ofGet("/login"), new LoginController());
        requestMap.put(Request.ofPost("/login"), new LoginController());
        requestMap.put(Request.ofGet("/register"), new RegisterController());
        requestMap.put(Request.ofPost("/register"), new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        return requestMap.get(Request.of(request));
    }

    public boolean isRequestMapped(final HttpRequest request) {
        return requestMap.containsKey(Request.of(request));
    }
}
