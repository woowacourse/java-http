package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private final Map<String, RequestHandler> handlers;
    private final RequestHandler defaultHandler;

    public RequestMapper(final RequestHandler defaultHandler) {
        this.handlers = new HashMap<>();
        this.defaultHandler = defaultHandler;
    }

    public void addHandler(final String path, final RequestHandler requestHandler) {
        handlers.put(path, requestHandler);
    }

    public RequestHandler findHandler(final String path) {
        return handlers.getOrDefault(path, defaultHandler);
    }
}
