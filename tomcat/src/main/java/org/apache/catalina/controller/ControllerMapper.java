package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private final Map<String, Controller> controllers = new HashMap<>();

    public void addController(final String path, final Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(final String path) {
        return controllers.get(path);
    }
}
