package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestMapping {

    private final List<Controller> controllers;

    public RequestMapping(final List<Controller> controllers) {
        if (controllers == null) {
            this.controllers = new ArrayList<>();
            return;
        }
        this.controllers = controllers;
    }

    public Controller getController(HttpRequest request) {
        return controllers.stream()
                          .filter(controller -> controller.support(request))
                          .findFirst()
                          .orElse(new FileController());
    }
}
