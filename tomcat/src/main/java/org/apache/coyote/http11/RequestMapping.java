package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {

    private final Map<String, Controller> controllers = new ConcurrentHashMap<>();
    private final Controller defaultController;

    public RequestMapping(final Controller defaultController) {
        this.defaultController = defaultController;
    }

    public void register(final String path, final Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
