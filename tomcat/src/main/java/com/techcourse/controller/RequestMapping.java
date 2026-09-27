package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/", new HelloController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private final Controller defaultController = new ResourceController();

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
