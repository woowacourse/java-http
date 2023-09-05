package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;

public class RequestMapping {

    private final List<Controller> controllers;

    public RequestMapping(final List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller getController(final HttpRequest httpRequest) {
        for (final Controller controller : controllers) {
            if (controller.canHandle(httpRequest)) {
                return controller;
            }
        }
        return null;
    }
}
