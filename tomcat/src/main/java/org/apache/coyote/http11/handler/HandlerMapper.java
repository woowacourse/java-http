package org.apache.coyote.http11.handler;

import org.apache.coyote.model.request.HttpRequest;

import java.util.Arrays;
import java.util.function.Function;

public enum HandlerMapper {

    LOGIN("/login.html", LoginHandler::new),
    INDEX("/index.html", IndexHandler::new),
    ;

    private final String path;
    private final Function<HttpRequest, Handler> handler;

    HandlerMapper(final String path, final Function<HttpRequest, Handler> handler) {
        this.path = path;
        this.handler = handler;
    }

    public static Handler findHandler(final HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return Arrays.stream(values())
                .filter(value -> path.equals(value.path))
                .findAny()
                .map(value -> value.handler.apply(httpRequest))
                .orElseGet(() -> new DefaultHandler(httpRequest));
    }
}
