package nextstep.jwp.controller;

import org.apache.coyote.model.request.HttpRequest;

import java.util.Arrays;

public enum HandlerMapper {

    DEFAULT("/", HomeHandler.getINSTANCE()),
    LOGIN("/login.html", LoginHandler.getINSTANCE()),
    INDEX("/index.html", IndexHandler.getINSTANCE()),
    REGISTER("/register.html", RegisterHandler.getINSTANCE()),
    ;

    private final String path;
    private final Handler handler;

    HandlerMapper(final String path, final Handler handler) {
        this.path = path;
        this.handler = handler;
    }

    public static Handler findHandler(final HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return Arrays.stream(values())
                .filter(value -> path.equals(value.path))
                .findAny()
                .map(value -> value.handler)
                .orElseGet(() -> DefaultHandler.getINSTANCE());
    }
}
