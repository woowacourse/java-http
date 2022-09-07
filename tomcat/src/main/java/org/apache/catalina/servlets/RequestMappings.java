package org.apache.catalina.servlets;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMappings {

    private final List<Controller> controllers;

    public RequestMappings(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller getAdaptiveController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.isProcessable(request))
                .findAny()
                .orElse(null);
    }
}
