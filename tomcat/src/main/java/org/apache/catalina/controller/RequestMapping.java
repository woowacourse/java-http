package org.apache.catalina.controller;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private List<Controller> controllers;

    public RequestMapping(final List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller getController(final HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(request))
                .findAny()
                .orElse(new DefaultController());
    }
}
