package com.techcourse.controller;

import java.util.Map;

public class ControllerResolver {
    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController(),
            "/", new HomeController()
    );

    public Controller resolve(String requestTarget) {
        return controllers.getOrDefault(requestTarget, new StaticController());
    }
}
