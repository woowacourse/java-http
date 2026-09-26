package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public void addController(final String path, final Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), new StaticResourceController());
    }
}
