package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public void addController(String path, Controller controller) {
        controllers.put(path, controller);
    }

    public Controller getController(HttpRequest request) {
        String requestPath = request.getRequestLine().getPath();
        int queryStartIndex = requestPath.indexOf('?');
        if (queryStartIndex >= 0) {
            requestPath = requestPath.substring(0, queryStartIndex);
        }
        return controllers.get(requestPath);
    }
}
