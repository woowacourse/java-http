package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController = new StaticResourceController();

    public void register(String path, Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
