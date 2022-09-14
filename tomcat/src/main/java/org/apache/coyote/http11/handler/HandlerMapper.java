package org.apache.coyote.http11.handler;

import static nextstep.jwp.handler.LoginHandler.LOGIN_HANDLER;
import static nextstep.jwp.handler.RegisterHandler.REGISTER_HANDLER;
import static nextstep.jwp.handler.get.HandlerForResource.HANDLER_FOR_GET_REQUEST;
import static nextstep.jwp.handler.get.LoginPageHandler.LOGIN_PAGE_HANDLER;

import java.util.Arrays;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;

public enum HandlerMapper {

    LOGIN_PAGE(HttpMethod.GET, "/login", LOGIN_PAGE_HANDLER),
    LOGIN(HttpMethod.POST, "/login", LOGIN_HANDLER),
    REGISTER(HttpMethod.POST, "/register", REGISTER_HANDLER);

    private final HttpMethod httpMethod;
    private final String uri;
    private final Handler handler;

    HandlerMapper(final HttpMethod httpMethod, final String uri, final Handler handler) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.handler = handler;
    }

    public static Handler findHandler(final HttpRequest request) {
        return Arrays.stream(values())
                .filter(mapper -> existHandler(request.getStartLine(), mapper))
                .findAny()
                .map(mapper -> mapper.handler)
                .orElse(HANDLER_FOR_GET_REQUEST);
    }

    private static boolean existHandler(final HttpRequestLine startLine, final HandlerMapper mapper) {
        return startLine.getMethod() == mapper.httpMethod &&
                startLine.getUri().equals(mapper.uri);
    }
}
