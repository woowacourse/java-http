package com.techcourse.controller;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class FrontController {

    private static final List<String> STATIC_PATH_SUFFIXES =
            List.of(".css", ".js", ".ico", ".html", ".svg");

    private static final Controller STATIC_CONTROLLER = new StaticController();
    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();

    private final Map<String, Controller> controllers = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller handle(final HttpRequest request) {
        final String path = request.getPath();
        if (isStaticPath(path)) {
            return STATIC_CONTROLLER;
        }
        return controllers.getOrDefault(path, NOT_FOUND_CONTROLLER);
    }

    private boolean isStaticPath(final String path) {
        return STATIC_PATH_SUFFIXES.stream().anyMatch(path::endsWith);
    }
}
