package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final Controller staticController = new StaticController();

    static {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public static Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticController);
    }
}
