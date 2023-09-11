package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class FrontController {
    private final Map<String, Controller> mappers;

    public FrontController() {
        mappers = new HashMap<>();
    }

    public Controller getController(final HttpRequest request) {
        String path = request.getPathValue();
        return mappers.getOrDefault(path, new ResourceController());
    }

    public void addController(String path, Controller controller) {
        mappers.put(path, controller);
    }
}
