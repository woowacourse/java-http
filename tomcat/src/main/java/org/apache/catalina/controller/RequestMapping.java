package org.apache.catalina.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController;

    public RequestMapping(Map<String, Controller> controllers, Controller defaultController) {
        this.controllers = Map.copyOf(controllers);
        this.defaultController = defaultController;
    }

    public Controller getController(HttpRequest request) {
        String path = request.requestLine().path();
        return controllers.getOrDefault(path, defaultController);
    }
}
