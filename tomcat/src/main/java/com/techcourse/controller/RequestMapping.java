package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> mappings;
    private final Controller defaultController;

    public RequestMapping(Map<String, Controller> mappings, Controller defaultController) {
        this.mappings = mappings;
        this.defaultController = defaultController;
    }

    public static RequestMapping createDefault() {
        LoginController loginController = new LoginController();
        RegisterController registerController = new RegisterController();

        return new RequestMapping(
                Map.of(
                        "/login", loginController,
                        "/login.html", loginController,
                        "/register", registerController,
                        "/register.html", registerController
                ),
                new StaticResourceController()
        );
    }

    public Controller getController(HttpRequest request) {
        return mappings.getOrDefault(request.getPath(), defaultController);
    }
}
