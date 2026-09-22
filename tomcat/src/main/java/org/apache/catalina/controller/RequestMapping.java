package org.apache.catalina.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(Map<String, Controller> controllers) {
        this.controllers = Map.copyOf(controllers);
    }

    public Controller getController(HttpRequest request) {
        return controllers.get(request.getPath());
    }
}
