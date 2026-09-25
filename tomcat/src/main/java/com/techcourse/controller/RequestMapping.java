package com.techcourse.controller;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    private final Controller staticResourceController = new StaticResourceController();

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
