package org.apache.coyote.http11;

import java.util.Map;

public final class RequestMapping {
    private final StaticResourceController staticResourceController;
    private final Map<String, Controller> controllers;

    public RequestMapping() {
        staticResourceController = new StaticResourceController();
        controllers = Map.of(
                "/register", new RegisterController(staticResourceController),
                "/login", new LoginController(staticResourceController)
        );
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.path(), staticResourceController);
    }
}
