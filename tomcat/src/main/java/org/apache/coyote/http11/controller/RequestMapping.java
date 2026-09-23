package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping() {
        this.controllers = Map.of("/login", new LoginController(),
                "/register", new RegisterController(),
                "/", new StaticResourceController());
    }

    public Controller getController(HttpRequest request) {
        String path = normalizePath(request.getPath());
        return controllers.getOrDefault(path, controllers.get("/"));
    }

    private String normalizePath(final String path) {
        return path.replaceFirst("(\\.html)?(\\?.*)?$", "");
    }
}
