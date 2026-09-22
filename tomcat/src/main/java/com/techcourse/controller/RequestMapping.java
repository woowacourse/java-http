package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> mappings;
    private final Controller defaultController;

    public RequestMapping(Map<String, Controller> mappings, Controller defaultController) {
        this.mappings = mappings;
        this.defaultController = defaultController;
    }

    public Controller getController(HttpRequest request) {
        return mappings.getOrDefault(request.getPath(), defaultController);
    }
}
