package nextstep.jwp.controller;

import org.apache.coyote.model.request.HttpRequest;

import java.util.Arrays;

public enum HandlerMapper {

    DEFAULT("/", HomeHandler.getINSTANCE()),
    LOGIN("/login", LoginHandler.getINSTANCE()),
    INDEX("/index", IndexHandler.getINSTANCE()),
    REGISTER("/register", RegisterHandler.getINSTANCE()),
    ;

    private final String path;
    private final Handler handler;

    HandlerMapper(final String path, final Handler handler) {
        this.path = path;
        this.handler = handler;
    }

    public static Handler findHandler(final HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if (DEFAULT.path.equals(path)) {
            return DEFAULT.handler;
        }
        return Arrays.stream(values())
                .filter(value -> !value.path.equals(DEFAULT.path))
                .filter(value -> path.startsWith(value.path))
                .findAny()
                .map(value -> value.handler)
                .orElseGet(DefaultHandler::getINSTANCE);
    }
}
