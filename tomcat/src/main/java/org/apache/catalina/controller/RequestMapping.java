package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static final Controller DEFAULT_CONTROLLER = new ResourceController();
    private static final Map<ControllerMappingInfo, Controller> CONTROLLERS = new HashMap<>();

    public Controller getController(final HttpRequest request) {
        return CONTROLLERS.getOrDefault(ControllerMappingInfo.from(request), DEFAULT_CONTROLLER);
    }

    public RequestMapping putController(final ControllerMappingInfo mappingInfo, final Controller controller) {
        CONTROLLERS.put(mappingInfo, controller);

        return this;
    }
}
