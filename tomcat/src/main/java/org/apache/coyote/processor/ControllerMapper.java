package org.apache.coyote.processor;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;

public class ControllerMapper {

    private final Map<String, Controller> controllers;

    public ControllerMapper() {
        this.controllers = Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController(),
                "/", new HomeController()
        );
    }

    public Controller getController(String path) {
        return controllers.get(path);
    }
}
