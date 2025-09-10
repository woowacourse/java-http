package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private final Map<String, Controller> controllers = new HashMap<>();

    public void addController(String path, Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(String path) {
        return controllers.get(path);
    }
}
