package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticController;

public class RequestMapper {

    private final Controller defaultController = new StaticController();
    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapper(final Map<String, Controller> controllers) {
        this.controllers.putAll(controllers);
    }

    public Controller getController(final String uri) {
        return controllers.getOrDefault(uri, defaultController);
    }
}
