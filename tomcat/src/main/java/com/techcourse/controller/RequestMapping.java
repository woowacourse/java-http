package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {
    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller staticResourceController = new StaticResourceController();

    public RequestMapping() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
