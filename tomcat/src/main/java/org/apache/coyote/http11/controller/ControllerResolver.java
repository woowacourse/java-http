package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerResolver {

    private final Map<String, Controller> controllers;
    private final Controller defaultController;

    public ControllerResolver(
            Map<String, Controller> controllers,
            Controller defaultController
    ) {
        this.controllers = Map.copyOf(controllers);
        this.defaultController = defaultController;
    }

    public Controller resolve(HttpRequest request) {
        return controllers.getOrDefault(
                request.getPath(),
                defaultController
        );
    }
}
