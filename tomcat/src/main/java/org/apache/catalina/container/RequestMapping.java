package org.apache.catalina.container;

import jakarta.controller.Controller;
import jakarta.http.HttpRequest;

import java.util.Map;
import java.util.Objects;

public class RequestMapping {

    private final Map<String, Controller> controllerMap;
    private final Controller defaultcontroller;

    public RequestMapping(Map<String, Controller> controllerMap, Controller defaultcontroller) {
        this.controllerMap = Objects.requireNonNull(controllerMap);
        this.defaultcontroller = Objects.requireNonNull(defaultcontroller);
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        return controllerMap.getOrDefault(path, defaultcontroller);
    }
}
