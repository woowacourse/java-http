package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {
    private final Map<String, Controller> requestMap = new HashMap<>();

    public RequestMapping() {
        requestMap.put("/login", new LoginController());
        requestMap.put("/register", new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        return requestMap.get(request.getRequestPath());
    }

    public boolean isRequestMapped(final HttpRequest request) {
        return requestMap.containsKey(request.getRequestPath());
    }
}
