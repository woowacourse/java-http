package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(String path) {
        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }
        if (getClass().getClassLoader().getResource("static" + path) == null) {
            return new NotFoundController();
        }
        return new DefaultController();
    }
}
