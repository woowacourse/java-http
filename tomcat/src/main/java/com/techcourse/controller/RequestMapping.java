package com.techcourse.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping() {
        this.controllers = Map.of(
                "/", new HelloController(),
                "/register", new RegisterController(),
                "/login", new LoginController()
        );
    }

    public Controller getController(HttpRequest httpRequest) {
        String urlPath = httpRequest.getUrlPath();
        if (this.controllers.containsKey(urlPath)) {
            return this.controllers.get(urlPath);
        }
        return new StaticResourceController();
    }
}
