package com.techcourse.controller;

import java.util.Map;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllers
            = Map.of(
                    "/", new IndexController(),
                    "/login", new LoginController(),
                    "/register", new RegisterController()
            );

    public static Controller getController(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();

        return controllers.getOrDefault(path, new ResourceController());
    }
}
