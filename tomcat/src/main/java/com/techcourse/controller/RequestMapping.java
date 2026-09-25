package com.techcourse.controller;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private static final String ROOT_PATH = "/";

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            ROOT_PATH, new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController());

    private static final Controller DEFAULT_CONTROLLER = new StaticResourceController();

    public Controller getController(final HttpRequest request) {
        if (request.isEmpty()) {
            return CONTROLLERS.get(ROOT_PATH);
        }
        return CONTROLLERS.getOrDefault(request.getPath(), DEFAULT_CONTROLLER);
    }
}
