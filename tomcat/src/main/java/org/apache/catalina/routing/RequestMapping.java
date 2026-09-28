package org.apache.catalina.routing;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController;

    public RequestMapping(final Controller defaultController) {
        this.defaultController = defaultController;
    }

    public void register(final String path, final Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(
                request.requestLine().path(), defaultController);
    }
}
