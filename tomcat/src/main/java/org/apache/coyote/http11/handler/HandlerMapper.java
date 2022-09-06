package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.handler.DefaultHandler.DEFAULT_HANDLER;
import static org.apache.coyote.http11.handler.LoginHandler.LOGIN_HANDLER;

import java.util.Arrays;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestStartLine;

public enum HandlerMapper {
    LOGIN(HttpMethod.POST, "/login", LOGIN_HANDLER);

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
                .orElse(DEFAULT_HANDLER);
    }

    private static boolean mapHandler(final HttpRequestStartLine startLine, final HandlerMapper mapper) {
        return startLine.getMethod() == mapper.httpMethod &&
                startLine.getUri().equals(mapper.uri);
    }
}
