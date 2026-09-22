package org.apache.catalina.controller;

import java.util.Map;
import java.util.Optional;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(Map<String, Controller> controllers) {
        this.controllers = Map.copyOf(controllers);
    }

    public Optional<Controller> findController(String path) {
        return Optional.ofNullable(controllers.get(path));
    }

}
