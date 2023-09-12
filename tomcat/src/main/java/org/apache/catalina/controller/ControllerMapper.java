package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerMapper {

    private final Map<String, Controller> controllers;

    public ControllerMapper(final Map<String, Controller> controllers) {
        this.controllers = new HashMap<>(controllers);
    }

    public Controller findController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), null);
    }
}
