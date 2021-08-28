package nextstep.jwp;

import java.util.Map;

public class ControllerMapping {

    final private Map<String, Controller> controllers;

    public ControllerMapping(Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller findByResource(String resource) {
        return controllers.getOrDefault(resource, new DefaultController("/"));
    }
}
