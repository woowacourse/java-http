package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {
    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController;

    public RequestMapping(Controller defaultController) {
        this.defaultController = defaultController;
    }

    public void register(String path, Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
