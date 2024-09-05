package org.apache.coyote.handler;

import java.util.Arrays;

public enum HandlerMapper {
    LOGIN("GET", "/login", new LoginHandler()),
    REGISTER("POST", "/register", new RegisterHandler()),
    ;

    private final String method;
    private final String path;
    private final Handler handler;

    HandlerMapper(String method, String path, Handler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public static Handler findHandler(String method, String path) {
        return Arrays.stream(values())
                .filter(handlerMapper -> handlerMapper.method.equals(method) && handlerMapper.path.equals(path))
                .map(handlerMapper -> handlerMapper.handler)
                .findFirst()
                .orElse(null);
    }
}
