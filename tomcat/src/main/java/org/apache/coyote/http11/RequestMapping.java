package org.apache.coyote.http11;

import org.apache.catalina.controller.Controller;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(final Map<String, Controller> controllers) {
        this.controllers = Map.copyOf(
                Objects.requireNonNull(controllers, "controllers는 null일 수 없습니다.")
        );
    }

    public Optional<Controller> getController(final HttpRequest request) {
        Objects.requireNonNull(request, "request는 null일 수 없습니다.");
        return Optional.ofNullable(controllers.get(request.path()));
    }
}
