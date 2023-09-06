package org.apache.handler;

import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {

    private static final Map<String, RequestHandler> handlers = new HashMap<>();
    private static final String QUERY_STRING = "?";

    static {
        handlers.put("/", new DefaultHandler());
        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new ResisterHandler());
    }

    private HandlerAdapter() {
    }

    public static RequestHandler findRequestHandler(String path) {
        if (isQueryString(path)) {
            path = extractPathWithoutQuery(path);
        }

        return handlers.getOrDefault(path, new FileHandler());
    }

    private static boolean isQueryString(String path) {
        return path.contains(QUERY_STRING);
    }

    private static String extractPathWithoutQuery(String path) {
        int index = path.indexOf(QUERY_STRING);
        path = path.substring(0, index);
        return path;
    }
}
