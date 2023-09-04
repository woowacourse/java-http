package org.apache.handler;

import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {

    private static final Map<String, RequestHandler> handlers = new HashMap<>();

    static {
        handlers.put("/", new DefaultHandler());
        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new ResisterHandler());
    }

    private HandlerAdapter() {
    }

    public static RequestHandler findRequestHandler(String path) {
        if (path.contains("?")) {
            int index = path.indexOf("?");
            path = path.substring(0, index);
        }

        return handlers.getOrDefault(path, new FileHandler());
    }
}
