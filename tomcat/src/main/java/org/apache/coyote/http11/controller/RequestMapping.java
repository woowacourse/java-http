package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

public class RequestMapping {

    private static final Controller NOT_FOUND =
            ((request, response) -> response.sendError(HttpStatus.NOT_FOUND));

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller staticResourceController;

    public RequestMapping(Controller staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    public void register(String path, Controller controller) {
        if (controllers.putIfAbsent(path, controller) != null) {
            throw new IllegalArgumentException("이미 등록한 경로: " + path);
        }
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        Controller controller = controllers.get(path);
        if (controller != null) {
            return controller;
        }

        if (isStaticResource(path)) {
            return staticResourceController;
        }

        return NOT_FOUND;
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js");
    }
}
