package org.apache.coyote.http11;

import java.util.Map;
import java.util.Objects;

class RequestMapping {

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
