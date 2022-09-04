package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.handler.LoginHandler;

public class RequestHandlerMapping {

    private final Map<String, RequestHandler> handlers;

    private RequestHandlerMapping(final Map<String, RequestHandler> handlers) {
        this.handlers = handlers;
    }

    public static RequestHandlerMapping init() {
        final Map<String, RequestHandler> handlers = new HashMap<>();

        handlers.put("/login", new LoginHandler());

        return new RequestHandlerMapping(handlers);
    }

    public boolean hasMappingHandler(final String handlerName) {
        return handlers.containsKey(handlerName);
    }

    public RequestHandler getHandler(final String handlerName) {
        return handlers.get(handlerName);
    }
}
