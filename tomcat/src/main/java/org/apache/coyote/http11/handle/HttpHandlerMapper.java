package org.apache.coyote.http11.handle;

import java.util.Set;
import org.apache.coyote.http11.handle.handler.custom.LoginHttpHandler;
import org.apache.coyote.http11.handle.handler.resource.CssHttpHandler;
import org.apache.coyote.http11.handle.handler.DefaultHttpHandler;
import org.apache.coyote.http11.handle.handler.resource.HtmlHttpHandler;
import org.apache.coyote.http11.handle.handler.HttpHandler;
import org.apache.coyote.http11.handle.handler.resource.JsHttpHandler;
import org.apache.coyote.http11.reqeust.HttpRequest;

public class HttpHandlerMapper {

    private static final HttpHandlerMapper instance = new HttpHandlerMapper();

    private final Set<HttpHandler> handlers = Set.of(
            DefaultHttpHandler.getInstance(),
            HtmlHttpHandler.getInstance(),
            CssHttpHandler.getInstance(),
            JsHttpHandler.getInstance(),
            LoginHttpHandler.getInstance()
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
