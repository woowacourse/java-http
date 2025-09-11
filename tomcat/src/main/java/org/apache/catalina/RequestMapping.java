package org.apache.catalina;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.auth.LoginController;
import org.apache.catalina.controller.auth.RegisterController;
import org.apache.catalina.controller.resource.StaticResourceController;
import org.apache.catalina.controller.root.HomeController;

public final class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController;

    public RequestMapping() {
        this.staticResourceController = new StaticResourceController();
        this.controllers = Map.of(
                "/", new HomeController(),
                "/login", new LoginController(new StaticResourceController()),
                "/register", new RegisterController(new StaticResourceController())
        );
    }

    public Controller getController(final String path) {
        return controllers.getOrDefault(path, staticResourceController);
    }
}
