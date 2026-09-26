package org.mvc.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Controller defaultController;
    private final Map<String, Controller> controllers;

    public RequestMapping() {
        this.defaultController = new StaticResourceController();
        this.controllers = new HashMap<>();
    }

    public void addController(final String path, final Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.path(), defaultController);
    }
}
