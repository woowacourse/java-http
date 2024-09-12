package com.techcourse.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapping {

    private final Map<String, Controller> handlers;

    public HandlerMapping(Map<String, Controller> handlers) {
        this.handlers = new HashMap<>(handlers);
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPathWithExtension();
        return handlers.get(path);
    }
}
