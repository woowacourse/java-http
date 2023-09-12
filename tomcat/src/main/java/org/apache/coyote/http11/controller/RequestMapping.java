package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final AuthController authController;
    private final RegisterController registerController;
    private final DefaultController defaultController;

    public RequestMapping(
            AuthController authController,
            RegisterController registerController,
            DefaultController defaultController
    ) {
        this.authController = authController;
        this.registerController = registerController;
        this.defaultController = defaultController;
    }

    public Controller getController(HttpRequest request) {
        if (request.path().equals("/login") || request.path().equals("/login.html")) {
            return authController;
        }
        if (request.path().equals("/register") || request.path().equals("/register.html")) {
            return registerController;
        }
        return defaultController;
    }
}
