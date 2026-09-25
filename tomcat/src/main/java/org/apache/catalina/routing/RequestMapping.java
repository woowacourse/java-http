package org.apache.catalina.routing;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(final Map<String, Controller> controllers) {
        this.controllers = Map.copyOf(controllers);
    }

    public Controller getController(final HttpRequest request) {
        return controllers.get(request.path());
    }
}
