package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class FrontController {
    private static final FrontController instance = new FrontController();
    private static final Map<String, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put("GET /login", new LoginController());
    }

    private FrontController() {
    }

    public static FrontController getInstance() {
        return instance;
    }

    public Controller mapController(HttpMethod method, String path) {
        return controllerMap.get("%s %s".formatted(method.getValue(), path));
    }
}
