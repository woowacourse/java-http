package org.apache.coyote.http11;

import java.util.Map;

class RequestMapping {

    private final Map<String, Controller> controllerMap;
    private final Controller defaultcontroller;

    public RequestMapping(Map<String, Controller> controllerMap, Controller defaultcontroller) {
        this.controllerMap = controllerMap;
        this.defaultcontroller = defaultcontroller;
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        return controllerMap.getOrDefault(path, defaultcontroller);
    }
}
