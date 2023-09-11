package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class FrontController {
    private static final Map<String, Controller> mappers = new HashMap<>();

    private FrontController() {
        throw new IllegalStateException("Utility class");
    }

    static {
        mappers.put("/", new HomeController());
        mappers.put("/login", new LoginController());
        mappers.put("/register", new RegisterController());
    }

    public static Controller getController(final HttpRequest request) {
        String path = request.getPathValue();
        return mappers.getOrDefault(path, new ResourceController());
    }
}
