package org.apache.coyote.http11.reqeust.handler;

import java.util.Set;
import org.apache.coyote.http11.reqeust.HttpRequest;

public class HttpRequestHandlerMapper {

    private static final HttpRequestHandlerMapper instance = new HttpRequestHandlerMapper();

    private final Set<HttpRequestHandler> handlers = Set.of(
            DefaultHttpRequestHandler.getInstance()
    );

    private HttpRequestHandlerMapper() {
    }

    public HttpRequestHandler getHandler(final HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 요청을 처리할 수 있는 핸들러가 없습니다. " + request));
    }

    public static HttpRequestHandlerMapper getInstance() {
        return instance;
    }
}
