package com.techcourse.controller;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapping;
import org.apache.catalina.resource.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public final class RequestMapping implements ControllerMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/", new HelloWorldController(),
            "/register", new RegisterController(),
            "/login", new LoginController(),
            "/session", new SessionController(),
            "/logout", new LogoutController()
    );
    private final Controller staticResourceController = new StaticResourceController();

    @Override
    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(
                request.getRequestUri().getPath(),
                staticResourceController
        );
    }
}
