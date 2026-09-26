package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/login.html", new LoginController(),
            "/register.html", new RegisterController()
    );
    private final Controller defaultController = new StaticResourceController();

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
