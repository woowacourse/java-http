package org.apache.coyote.http11.handler;

import java.util.Arrays;

public enum HandlerMapper {

    HOME("/", new HomeHandler()),
    LOGIN("/login", new LoginHandler()),
    ;

    private final String uri;
    private final Handler handler;

    HandlerMapper(final String uri, final Handler handler) {
        this.uri = uri;
        this.handler = handler;
    }

    public static Handler lookUp(final String uri) {
        return Arrays.stream(values())
                .filter(value -> value.uri.equals(uri))
                .map(handlerMapper -> handlerMapper.handler)
                .findFirst()
                .orElse(new ResourceHandler());
    }
}
