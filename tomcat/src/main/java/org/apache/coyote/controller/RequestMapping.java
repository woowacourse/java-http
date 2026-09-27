package org.apache.coyote.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController;

    public RequestMapping() {
        this(Map.of());
    }

    public RequestMapping(Map<String, Controller> controllers) {
        this.controllers.putAll(controllers);

        defaultController = new StaticResourceController();
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
