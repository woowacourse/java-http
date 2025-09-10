package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.handler.HttpResourceHandler;

public class Resolver {

    private final Map<String, HttpHandler> routes = new LinkedHashMap<>();

    private final HttpResourceHandler httpResourceHandler;

    public Resolver(final HttpResourceHandler httpResourceHandler) {
        this.httpResourceHandler = httpResourceHandler;
    }

    public Resolver register(final String path, final HttpHandler httpHandler) {
        routes.put(path, httpHandler);
        return this;
    }

    public HttpHandler resolve(final String path) {
        if (!routes.containsKey(path)) {
            return httpResourceHandler;
        }
        return routes.get(path);
    }
}
