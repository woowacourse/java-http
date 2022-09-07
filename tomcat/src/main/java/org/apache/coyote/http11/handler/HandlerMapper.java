package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.handler.HandlerForGetRequest.HANDLER_FOR_GET_REQUEST;
import static org.apache.coyote.http11.handler.LoginHandler.LOGIN_HANDLER;
import static org.apache.coyote.http11.handler.LoginPageHandler.LOGIN_PAGE_HANDLER;
import static org.apache.coyote.http11.handler.RegisterHandler.REGISTER_HANDLER;

import java.util.Arrays;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestStartLine;

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
                .filter(mapper -> mapHandler(request.getStartLine(), mapper))
                .findAny()
                .map(mapper -> mapper.handler)
                .orElse(HANDLER_FOR_GET_REQUEST);
    }

    private static boolean mapHandler(final HttpRequestStartLine startLine, final HandlerMapper mapper) {
        return startLine.getMethod() == mapper.httpMethod &&
                startLine.getUri().equals(mapper.uri);
    }
}
