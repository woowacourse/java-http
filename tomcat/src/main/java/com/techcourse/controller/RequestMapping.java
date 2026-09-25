package com.techcourse.controller;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController());

    private static final Controller DEFAULT_CONTROLLER = new StaticResourceController();

    public Controller getController(final HttpRequest request) {
        return CONTROLLERS.getOrDefault(request.getPath(), DEFAULT_CONTROLLER);
    }
}
