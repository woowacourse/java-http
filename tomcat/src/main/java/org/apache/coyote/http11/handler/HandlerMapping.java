package org.apache.coyote.http11.handler;

import java.util.Arrays;
import java.util.function.Function;
import org.apache.coyote.http11.model.HttpRequest;

public enum HandlerMapping {

    LOGIN("/login", LoginHandler::new),
    INDEX("/index.html", IndexHandler::new),
    HOME("/", HomeHandler::new)
    ;

    private final String path;
    private final Function<HttpRequest, Handler> expression;

    HandlerMapping(final String path, final Function<HttpRequest, Handler> expression) {
        this.path = path;
        this.expression = expression;
    }

    public static Handler findHandler(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return Arrays.stream(values())
                .filter(i -> path.equals(i.path))
                .findAny()
                .map(i -> i.expression.apply(httpRequest))
                .orElse(new FileHandle(httpRequest));
    }
}
