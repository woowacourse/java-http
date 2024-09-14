package com.techcourse.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> handlers;

    public RequestMapping(Map<String, Controller> handlers) {
        this.handlers = new HashMap<>(handlers);
    }

    public Optional<Controller> getController(HttpRequest request) {
        String path = request.getPathWithExtension();

        return Optional.ofNullable(handlers.get(path));
    }
}
