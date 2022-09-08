package org.apache.coyote.http11.controller;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerContainer {

    private static final Controller resourceFileController = new ResourceFileController();

    private final List<Controller> controllers;

    public ControllerContainer(final List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller find(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(it -> it.containsPath(httpRequest.getRequestUri().getResourcePath()))
                .findFirst()
                .orElse(resourceFileController);
    }
}
