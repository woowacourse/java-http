package org.apache.catalina.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;

public final class RequestMapping {
    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController;

    public RequestMapping(final Controller defaultController) {
        this.defaultController = Objects.requireNonNull(defaultController);
    }

    public void addMapping(final String path, final Controller controller) {
        controllers.put(Objects.requireNonNull(path), Objects.requireNonNull(controller));
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.path(), defaultController);
    }
}
