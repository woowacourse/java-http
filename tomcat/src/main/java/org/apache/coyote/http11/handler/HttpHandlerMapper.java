package org.apache.coyote.http11.handler;

import java.util.Set;
import org.apache.coyote.http11.reqeust.HttpRequest;

public class HttpHandlerMapper {

    private static final HttpHandlerMapper instance = new HttpHandlerMapper();

    private final Set<HttpHandler> handlers = Set.of(
            DefaultHttpHandler.getInstance(),
            HtmlHttpHandler.getInstance(),
            CssHttpHandler.getInstance(),
            JsHttpHandler.getInstance()
    );

    private HttpHandlerMapper() {
    }

    public HttpHandler getHandler(final HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 요청을 처리할 수 있는 핸들러가 없습니다. " + request));
    }

    public static HttpHandlerMapper getInstance() {
        return instance;
    }
}
