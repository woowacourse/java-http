package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public void addController(
            final String path,
            final Controller controller
    ) {
        controllers.put(path, controller);
    }

    public Optional<Controller> getController(final HttpRequest request) {
        return Optional.ofNullable(
                controllers.get(request.getPath())
        );
    }
}
