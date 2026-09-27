package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController = new StaticResourceController();

    public RequestMapping() {
        Controller loginController = new LoginController();
        Controller registerController = new RegisterController();
        controllers.put("/login", loginController);
        controllers.put("/login.html", loginController);
        controllers.put("/register", registerController);
        controllers.put("/register.html", registerController);
    }

    public Controller getController(HttpRequest request) {
        if (!request.getMethod().equals("GET") && !request.getMethod().equals("POST")) {
            return defaultController;
        }
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
