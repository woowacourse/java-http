package org.apache.coyote.http11.handler;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.coyote.http11.model.request.HttpRequest;

public enum HandlerMapping {

    LOGIN("/login", LoginHandler::new),
    REGISTER("/register", RegisterHandler::new),
    INDEX("/index.html", IndexHandler::new),
    HOME("/", HomeHandler::new),
    ;

    private final String path;
    private final Function<HttpRequest, Handler> expression;

    HandlerMapping(final String path, final Function<HttpRequest, Handler> expression) {
        this.path = path;
        this.expression = expression;
    }

    public static Handler findHandler(final HttpRequest httpRequest) {
        String path = httpRequest.getRequestTarget();
        return Arrays.stream(values())
                .filter(matchHandler(path))
                .findAny()
                .map(mapToHandler(httpRequest))
                .orElseGet(() -> new ResourceHandler(httpRequest));
    }

    private static Predicate<HandlerMapping> matchHandler(final String path) {
        return handlerMapping -> path.equals(handlerMapping.path);
    }

    private static Function<HandlerMapping, Handler> mapToHandler(final HttpRequest httpRequest) {
        return handlerMapping -> handlerMapping.expression
                .apply(httpRequest);
    }
}
