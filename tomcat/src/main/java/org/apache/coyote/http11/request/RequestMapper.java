package org.apache.coyote.http11.request;

import org.apache.coyote.controller.Controller;

import java.util.Map;

public class RequestMapper {
    private final Map<String, Controller> controllers;

    public RequestMapper(final Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller mappingWithController(final HttpRequest request) {
        return controllers.get(request.getPath());
    }
}
