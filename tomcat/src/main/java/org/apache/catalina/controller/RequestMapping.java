package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController;

    public RequestMapping(final Map<String, Controller> controllers, final Controller defaultController) {
        this.controllers = Map.copyOf(controllers);
        this.defaultController = defaultController;
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
