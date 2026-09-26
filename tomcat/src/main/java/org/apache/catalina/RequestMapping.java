package org.apache.catalina;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController;

    public RequestMapping(Map<String, Controller> controllers, Controller defaultController) {
        this.controllers = Map.copyOf(controllers);
        this.defaultController = defaultController;
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
