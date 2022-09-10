package org.apache.catalina.core;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.ResourceController;

public class RequestMapping {

    private final static Map<String, Controller> CONTROLLERS = new HashMap<>();

    public void setController(final String path, final Controller controller) {
        CONTROLLERS.put(path, controller);
    }

    public Controller getController(final String path) {
        if (CONTROLLERS.containsKey(path)) {
            return CONTROLLERS.get(path);
        }
        return new ResourceController();
    }
}
