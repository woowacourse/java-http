package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticController;

    public RequestMapping() {
        controllers = new HashMap<>();

        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());

        staticController = new StaticController();
    }

    public Controller getController(HttpRequest request) {
        if (controllers.containsKey(request.getPath())) {
            return controllers.get(request.getPath());
        }
        return staticController;
    }
}
