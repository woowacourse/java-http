package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.request.HttpRequest;

public class HandlerFinder {
    private static final Handler DEFAULT_HANDLER;
    private static final Map<String, Handler> handlers;

    static {
        handlers = new HashMap<>();
        handlers.put("/", new HelloWorldHandler());
        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new RegisterHandler());

        DEFAULT_HANDLER = new DefaultHandler();
    }

    public static Handler find(HttpRequest request) {
        String path = request.getUri().getDetail();
        return handlers.getOrDefault(path, DEFAULT_HANDLER);
    }
}
