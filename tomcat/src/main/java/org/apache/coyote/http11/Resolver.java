package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.handler.HttpResourceController;

public class Resolver {

    private final Map<String, Controller> routes = new LinkedHashMap<>();

    private final HttpResourceController httpResourceController;

    public Resolver(final HttpResourceController httpResourceController) {
        this.httpResourceController = httpResourceController;
    }

    public Resolver register(final String path, final Controller controller) {
        routes.put(path, controller);
        return this;
    }

    public Controller resolve(final String path) {
        if (!routes.containsKey(path)) {
            return httpResourceController;
        }
        return routes.get(path);
    }
}
