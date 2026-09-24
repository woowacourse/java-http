package org.apache.catalina.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public final class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController;

    public RequestMapping(Map<String, Controller> controllers, Controller defaultController) {
        this.controllers = Map.copyOf(controllers);
        this.defaultController = defaultController;
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.path(), defaultController);
    }
}
