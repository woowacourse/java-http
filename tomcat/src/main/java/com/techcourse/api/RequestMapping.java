package com.techcourse.api;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<RequestKey, Controller> controllers = new HashMap<>();

    public void add(
            final String method,
            final String path,
            final Controller controller
    ) {
        controllers.put(new RequestKey(method, path), controller);
    }

    public Controller getController(
            final String method,
            final String path
    ) {
        return controllers.get(new RequestKey(method, path));
    }
}
