package org.apache.catalina;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.resource.StaticResourceController;

public final class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController;

    public RequestMapping(
            final Map<String, Controller> controllers,
            final StaticResourceController staticResourceController
    ) {
        this.controllers = controllers;
        this.staticResourceController = staticResourceController;
    }

    public Controller getController(final String path) {
        return controllers.getOrDefault(path, staticResourceController);
    }
}
