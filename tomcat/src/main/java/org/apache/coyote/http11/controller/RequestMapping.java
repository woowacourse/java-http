package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.request.HttpRequest;

public final class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController;

    public RequestMapping(Manager sessionManager) {
        this.controllers = Map.of(
                "/login", new LoginController(sessionManager),
                "/register", new RegisterController(),
                "/", new RootController()
        );
        this.staticResourceController = new StaticResourceController();
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
