package org.apache.catalina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.MappedController;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController;

    public RequestMapping(Controller defaultController, List<MappedController> controllers) {
        this.defaultController = defaultController;

        Map<String, Controller> mappings = new HashMap<>();
        for (MappedController controller : controllers) {
            mappings.put(controller.getPath(), controller);
        }
        this.controllers = Map.copyOf(mappings);
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
