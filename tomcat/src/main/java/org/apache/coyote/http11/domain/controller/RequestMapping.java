package org.apache.coyote.http11.domain.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.domain.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping() {
        this.controllers = new HashMap<>();
    }

    public RequestMapping(Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller getController(HttpRequest request) {
        return controllers.get(request.getPath());
    }

    public boolean canHandle(HttpRequest request) {
        return controllers.containsKey(request.getPath());
    }
}

