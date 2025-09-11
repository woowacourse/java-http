package org.apache.catalina.controller;

import com.techcourse.controller.StaticResourceController;
import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller staticResourceController = new StaticResourceController();

    public void addController(String path, Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(String path) {
        return controllers.getOrDefault(path, staticResourceController);
    }
}
