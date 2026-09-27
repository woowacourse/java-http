package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();
    private final Map<String, Controller> controllers = new HashMap<>();

    public Controller getController(HttpRequest request) {
        if (!controllers.containsKey(request.getPath())) {
            return controllers.get("static");
        }
        return controllers.get(request.getPath());
    }

    private void initController() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        controllers.put("static", new StaticResourceController());
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    private RequestMapping() {
        initController();
    }
}
