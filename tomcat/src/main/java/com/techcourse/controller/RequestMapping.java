package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static final Map<String, Controller> requestMapper = new HashMap<>();

    static {
        requestMapper.put("/login", new LoginController());
        requestMapper.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        if (requestMapper.containsKey(request.getPath())) {
            return requestMapper.get(request.getPath());
        }
        return new StaticResourceController();
    }
}
