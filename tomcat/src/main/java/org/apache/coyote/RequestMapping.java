package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;

import java.util.Map;
import java.util.Optional;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(
            final Map<String, Controller> controllers
    ) {
        this.controllers =
                Map.copyOf(
                        controllers
                );
    }

    public Optional<Controller> getController(
            final HttpRequest request
    ) {
        return Optional.ofNullable(
                controllers.get(
                        request.getPath()
                )
        );
    }
}