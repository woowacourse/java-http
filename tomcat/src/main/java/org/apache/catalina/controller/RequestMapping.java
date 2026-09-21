package org.apache.catalina.controller;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(Map<String, Controller> controllers) {
        this.controllers = Map.copyOf(controllers);
    }

    public Optional<Controller> getController(HttpRequest request) {
        return Optional.ofNullable(controllers.get(request.path()));
    }
}
