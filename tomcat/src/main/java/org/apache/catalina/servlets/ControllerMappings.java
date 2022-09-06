package org.apache.catalina.servlets;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerMappings {

    private final List<Controller> controllers;

    public ControllerMappings(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller getAdaptiveController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.isProcessable(request))
                .findAny()
                .orElse(null);
    }
}
