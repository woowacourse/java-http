package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController = new StaticResourceController();

    public RequestMapping() {
        UserService userService = new UserService();
        this.controllers = Map.of(
                "/login.html", new LoginController(userService),
                "/register.html", new RegisterController(userService)
        );
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
