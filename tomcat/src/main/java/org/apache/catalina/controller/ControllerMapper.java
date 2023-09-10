package org.apache.catalina.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerMapper {

    private final List<Controller> controllers;

    public ControllerMapper(final List<Controller> controllers) {
        this.controllers = new ArrayList<>(controllers);
    }

    public Controller findController(final HttpRequest request) {
        return controllers.stream()
                .filter(each -> each.supports(request))
                .findAny()
                .orElse(null);
    }
}
