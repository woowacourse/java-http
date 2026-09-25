package org.apache.coyote.routing;

import java.util.Map;
import java.util.Optional;

public class RequestMapping {

    private final Map<RouteKey, Controller> handlers;

    public RequestMapping(Map<RouteKey, Controller> handlers) {
        this.handlers = handlers;
    }

    public Optional<Controller> getHandler(RouteKey routeKey) {
        return Optional.ofNullable(handlers.get(routeKey));
    }

    public void add(RouteKey routeKey, Controller controller) {
        Controller existHandler = handlers.putIfAbsent(routeKey, controller);
        if (existHandler != null) {
            throw new IllegalStateException("Controller already exists: " + routeKey);
        }
    }
}
