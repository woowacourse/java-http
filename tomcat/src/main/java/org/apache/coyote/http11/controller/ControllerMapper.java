package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private static final Map<String, Controller> CONTROLLER_MAPPER = new HashMap<>();

    static {
        CONTROLLER_MAPPER.put("/login", new LoginController());
        CONTROLLER_MAPPER.put("/register", new RegisterController());
    }

    private ControllerMapper() {
    }

    public static Controller map(String path) {
        if (CONTROLLER_MAPPER.containsKey(path)) {
            return CONTROLLER_MAPPER.get(path);
        }
        return StaticResourceController.getInstance();
    }
}
