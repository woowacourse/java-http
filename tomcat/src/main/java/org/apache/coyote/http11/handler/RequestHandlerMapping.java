package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;

public class RequestHandlerMapping {

    private final Map<String, RequestHandler> handlers;

    private RequestHandlerMapping(Map<String, RequestHandler> handlers) {
        this.handlers = handlers;
    }

    public static RequestHandlerMapping init() {
        final Map<String, RequestHandler> handlers = new HashMap<>();

        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new RegisterHandler());

        return new RequestHandlerMapping(handlers);
    }

    public boolean acceptHandler(String path) {
        return handlers.containsKey(path);
    }

    public RequestHandler getHandler(String path) {
        return handlers.get(path);
    }
}
