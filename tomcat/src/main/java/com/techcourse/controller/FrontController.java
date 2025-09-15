package com.techcourse.controller;

import java.util.Map;

public class FrontController {

    private static final Map<String, Controller> controllers = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    private FrontController() {
    }

    public static Controller getController(String requestPath) {
        return controllers.getOrDefault(requestPath, new DefaultController());
    }
}
