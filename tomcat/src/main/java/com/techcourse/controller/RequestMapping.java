package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller staticResourceController = new StaticResourceController();

    public RequestMapping() {
        initializeControllers();
    }

    private void initializeControllers() {
        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        if (isStaticResource(path)) {
            return staticResourceController;
        }

        return controllers.entrySet().stream()
                .filter(entry -> path.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new NotFoundController());
    }

    private boolean isStaticResource(String path) {
        return path.matches(".+\\.(css|js|ico|html)$");
    }
}
