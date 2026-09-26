package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController;

    public RequestMapping() {
        this(new StaticResourceController());
    }

    public RequestMapping(final Controller defaultController) {
        this.defaultController = defaultController;
    }

    public RequestMapping register(final String path, final Controller controller) {
        controllers.put(path, controller);
        return this;
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
