package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerResolver {

    private static final Controller STATIC_RESOURCE_CONTROLLER =
            new StaticResourceController();

    private static final Map<String, Controller> CONTROLLERS =
            Map.of(
                    "/", new RootController(),
                    "/login", new LoginController(),
                    "/register", new RegisterController()
            );

    private ControllerResolver() {
    }

    public static Controller resolve(HttpRequest request) {
        return CONTROLLERS.getOrDefault(
                request.getPath(),
                STATIC_RESOURCE_CONTROLLER
        );
    }
}
