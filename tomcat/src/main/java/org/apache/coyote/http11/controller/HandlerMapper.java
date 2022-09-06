package org.apache.coyote.http11.controller;

import java.util.Arrays;

public enum HandlerMapper {

    HOME("/", new HomeController()),
    LOGIN("/login", new LoginController()),
    ;

    private final String uri;
    private final Controller controller;

    HandlerMapper(final String uri, final Controller controller) {
        this.uri = uri;
        this.controller = controller;
    }

    public static Controller lookUp(final String uri) {
        return Arrays.stream(values())
                .filter(value -> value.uri.equals(uri))
                .map(handlerMapper -> handlerMapper.controller)
                .findFirst()
                .orElse(new ResourceController());
    }
}
