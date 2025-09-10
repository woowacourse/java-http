package org.apache.catalina.controller;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping() {
        this.controllers = initControllers();
    }

    private Map<String, Controller> initControllers() {
        return Map.of(
                "/", new HomeController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        return controllers.get(path);
    }
}
