package org.apache.coyote.http11.controller;

import java.util.Map;

public class RequestMapping {

    private static final Controller STATIC_RESOURCE_CONTROLLER = new StaticPageController();
    private static final Controller LOGIN_CONTROLLER = new LoginController();
    private static final Map<String, Controller> controllers = Map.of(
            "/", LOGIN_CONTROLLER,
            "/login", LOGIN_CONTROLLER,
            "/logout", new LogoutController(),
            "/register", new RegisterController()
    );

    private RequestMapping() {
    }

    public static Controller getController(String uri) {
        return controllers.getOrDefault(uri, STATIC_RESOURCE_CONTROLLER);
    }
}
