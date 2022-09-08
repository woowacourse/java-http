package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> mappings;

    public RequestMapping(Map<String, Controller> mappings) {
        this.mappings = mappings;
    }

    public static RequestMapping of(Controller... controllers) {
        Map<String, Controller> mappings = new HashMap<>();
        for (final var controller : controllers) {
            mapPathToController(mappings, controller);
        }
        return new RequestMapping(mappings);
    }

    private static void mapPathToController(Map<String, Controller> mappings, Controller controller) {
        for (final var path : controller.getMappedPaths()) {
            mappings.put(path, controller);
        }
    }

    public Controller getController(HttpRequest request) {
        final var uri = request.getUri();
        return mappings.get(uri);
    }

    public boolean hasMappedController(HttpRequest request) {
        final var uri = request.getUri();
        return mappings.containsKey(uri);
    }
}
