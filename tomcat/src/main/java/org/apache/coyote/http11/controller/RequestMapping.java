package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(final Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller getController(HttpRequest request) {
        String path = normalizePath(request.getPath());
        return controllers.getOrDefault(path, controllers.get("/"));
    }

    private String normalizePath(final String path) {
        return path.replaceFirst("(\\.html)?(\\?.*)?$", "");
    }
}
