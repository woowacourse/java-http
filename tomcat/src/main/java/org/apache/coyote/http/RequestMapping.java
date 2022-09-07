package org.apache.coyote.http;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestMapping {

    private final Set<Controller> controllers;

    private RequestMapping(final Set<Controller> controllers) {
        this.controllers = controllers;
    }

    public static RequestMapping of(final Controller... controllers) {
        final Set<Controller> controllerSet = new HashSet<>(List.of(controllers));

        return new RequestMapping(controllerSet);
    }

    public Controller find(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(it -> it.isMatch(httpRequest))
                .findFirst()
                .orElseGet(GeneralController::new);
    }
}
