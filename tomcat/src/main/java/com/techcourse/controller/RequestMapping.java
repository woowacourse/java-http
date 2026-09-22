package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;

import java.util.Map;
import java.util.Objects;

public final class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController;

    public RequestMapping(
            final Map<String, Controller> controllers,
            final Controller defaultController
    ) {
        this.controllers = Map.copyOf(controllers);
        this.defaultController = Objects.requireNonNull(defaultController);
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.path(), defaultController);
    }
}
