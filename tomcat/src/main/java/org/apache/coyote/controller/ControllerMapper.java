package org.apache.coyote.controller;

import org.apache.coyote.http11.request.Request;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private static final Controller STATIC_RESOURCE_CONTROLLER = new StaticResourceController();

    private static final Map<String, Controller> CONTROLLER_MAP = new HashMap<>();

    private ControllerMapper() {
    }

    public static void register(final String path,
                                final Controller controller) {
        CONTROLLER_MAP.put(path, controller);
    }

    public static Controller getController(final Request request) {
        final String path = request.getRequestLine().getRequestPath();
        if (path.contains(".")) {
            return STATIC_RESOURCE_CONTROLLER;
        }
        return CONTROLLER_MAP.get(path);
    }
}
