package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.StaticResourceController;

public class RequestMapping {

    private final Map<String, Controller> controllerMap = new HashMap<>();
    private final Controller staticResourceController = new StaticResourceController();

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        return controllerMap.getOrDefault(path, staticResourceController);
    }

    public void addController(String path, Controller controller) {
        controllerMap.put(path, controller);
    }
}
