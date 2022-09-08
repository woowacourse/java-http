package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.request.HttpRequest;

public class HandlerMapping {

    private final Map<String, Controller> mappings;

    public HandlerMapping(Map<String, Controller> mappings) {
        this.mappings = mappings;
    }

    public static HandlerMapping of(Controller... controllers) {
        Map<String, Controller> mappings = new HashMap<>();
        for (final var controller : controllers) {
            mapPathToController(mappings, controller);
        }
        return new HandlerMapping(mappings);
    }

    private static void mapPathToController(Map<String, Controller> mappings, Controller controller) {
        for (final var path : controller.getMappedPaths()) {
            mappings.put(path, controller);
        }
    }

    public Controller getMappedHandler(HttpRequest request) {
        final var uri = request.getUri();
        return mappings.get(uri);
    }

    public boolean hasMappedHandler(HttpRequest request) {
        final var uri = request.getUri();
        return mappings.containsKey(uri);
    }
}
