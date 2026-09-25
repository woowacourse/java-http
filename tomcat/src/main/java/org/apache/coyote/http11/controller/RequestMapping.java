package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLER_MAP = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController(),
            "/", new RootController()
    );
    private static final Controller DEFAULT_CONTROLLER = new DefaultController();

    public Controller getController(HttpRequest request) {
        return CONTROLLER_MAP.getOrDefault(request.getPath(), DEFAULT_CONTROLLER);
    }
}
