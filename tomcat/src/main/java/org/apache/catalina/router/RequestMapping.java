package org.apache.catalina.router;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> mappings;

    public RequestMapping() {
        mappings = new HashMap<>();
    }

    public void addMapping(final String path, final Controller controller) {
        mappings.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        return mappings.getOrDefault(request.getPath(), getStaticResourceController());
    }

    public Controller getStaticResourceController() {
        return mappings.values().stream()
                .filter(controller -> controller.getClass() == StaticResourceController.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("StaticResourceController is not registered."));
    }
}
