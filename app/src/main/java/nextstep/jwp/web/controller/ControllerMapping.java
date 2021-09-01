package nextstep.jwp.web.controller;

import java.util.Map;

public class ControllerMapping {

    private final Map<String, Controller> controllers;

    public ControllerMapping(Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller findByResource(String resource) {
        return controllers.getOrDefault(resource, new DefaultController("/"));
    }
}
