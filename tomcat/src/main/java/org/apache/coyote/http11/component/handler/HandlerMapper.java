package org.apache.coyote.http11.component.handler;

import java.util.HashMap;
import java.util.Map;

public class HandlerMapper {

    private static final Map<String, HttpHandler> registry;

    static {
        registry = new HashMap<>();
        HandlerMapper.add("/", new HomeHandler("/"));
        HandlerMapper.add("/login", new LoginHandler("/login"));
    }

    private HandlerMapper() {
    }

    public static void add(final String uriPath, final HttpHandler handler) {
        registry.put(uriPath, handler);
    }

    public static HttpHandler get(final String uriPath) {
        if (!registry.containsKey(uriPath)) {
            return new StaticResourceHandler(uriPath);
        }
        return registry.get(uriPath);
    }
}
