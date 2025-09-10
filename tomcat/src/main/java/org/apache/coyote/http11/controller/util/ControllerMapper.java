package org.apache.coyote.http11.controller.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.IndexController;

public class ControllerMapper {

    private static final Map<String, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put("/index.html", new IndexController());
    }

    public static Controller getController(final String path) {
        return CONTROLLERS.getOrDefault(path, null);
    }
}
