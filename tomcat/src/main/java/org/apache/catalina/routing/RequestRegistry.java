package org.apache.catalina.routing;

import java.util.Map;
import java.util.Optional;

public class RequestRegistry {

    private final Map<RouteKey, RequestHandler> handlers;

    public RequestRegistry(Map<RouteKey, RequestHandler> handlers) {
        this.handlers = handlers;
    }

    public Optional<RequestHandler> getHandler(RouteKey routeKey) {
        return Optional.ofNullable(handlers.get(routeKey));
    }

    public void add(RouteKey routeKey, RequestHandler requestHandler) {
        RequestHandler existHandler = handlers.putIfAbsent(routeKey, requestHandler);
        if (existHandler != null) {
            throw new IllegalStateException("Controller already exists: " + routeKey);
        }
    }
}
