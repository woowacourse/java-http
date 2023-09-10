package org.apache.coyote;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ResourceController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.MainController;
import org.apache.catalina.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new MainController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    private static final Controller FILE_CONTROLLER = new ResourceController();

    public static Controller getController(HttpRequest request) {
        final String uri = request.getUri();

        if (CONTROLLERS.containsKey(uri)) {
            return CONTROLLERS.get(uri);
        }

        return FILE_CONTROLLER;
    }
}

