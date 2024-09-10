package org.apache.coyote.controller;

import java.util.Arrays;
import org.apache.coyote.http.request.HttpRequest;

public enum RequestMapping {
    LOGIN("POST", "/login", new LoginHandler()),
    REGISTER("POST", "/register", new RegisterHandler()),
    ;

    private final String method;
    private final String path;
    private final Handler handler;

    RequestMapping(String method, String path, Handler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public static Handler findHandler(HttpRequest request) {
        return findHandler(request.getMethod(), request.getUri());
    }

    private static Handler findHandler(String method, String path) {
        return Arrays.stream(values())
                .filter(handlerMapper -> handlerMapper.method.equals(method) && handlerMapper.path.equals(path))
                .map(handlerMapper -> handlerMapper.handler)
                .findFirst()
                .orElse(null);
    }
}
