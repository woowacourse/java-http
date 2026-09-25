package org.apache.catalina.controller;

import java.util.Map;
import java.util.Optional;

public class ControllerMapping {

    private final Map<String, Controller> controllers;

    public ControllerMapping(final Map<String, Controller> controllers) {
        this.controllers = Map.copyOf(controllers);
    }

    public Optional<Controller> find(final String path) {
        return Optional.ofNullable(controllers.get(path));
    }
}
