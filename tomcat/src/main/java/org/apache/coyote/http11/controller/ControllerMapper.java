package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerMapper {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final Controller staticResourceController = new StaticResourceController();

    static {
        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        if (controllers.containsKey(request.getPath())) {
            return controllers.get(request.getPath());
        }
        return staticResourceController;
    }
}
