package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerContainer {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultStaticController;

    public ControllerContainer() {
        this.defaultStaticController = new StaticResourceController();
        initializeDefaultControllers();
    }

    private void initializeDefaultControllers() {
        addControllerWithPath("/", new DefaultController());
    }

    public void addControllerWithPath(String path, Controller controller) {
        controllers.put(path, controller);
    }

    public Controller findController(String path) {
        Controller controller = controllers.get(path);
        if (controller != null) {
            return controller;
        }
        
        return defaultStaticController;
    }
}
