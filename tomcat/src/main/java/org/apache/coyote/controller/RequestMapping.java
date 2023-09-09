package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put("/", new IndexController());
        controllers.put("/index.html", new IndexController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        if (controllers.get(path) == null) {
            return controllers.get("/");
        }
        return controllers.get(path);
    }
}
