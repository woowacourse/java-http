package com.http.application;

import com.http.application.handle.RequestHandler;
import com.http.application.handle.impl.LoginRequestHandler;
import com.http.domain.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public final class RequestHandlerMapper {

    private static final Map<String, RequestHandler> handlers = new HashMap<>();

    private RequestHandlerMapper() {
    }

    static {
        handlers.put("/login", new LoginRequestHandler());
    }

    public static void handle(HttpRequest httpRequest) {
        final String path = httpRequest.startLine().path();

        if (handlers.containsKey(path)) {
            handlers.get(path).handle(httpRequest);
        }
    }
}
