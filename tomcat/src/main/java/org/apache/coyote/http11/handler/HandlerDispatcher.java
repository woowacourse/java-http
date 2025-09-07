package org.apache.coyote.http11.handler;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class HandlerDispatcher {

    private final List<RequestHandler> handlers;

    public HandlerDispatcher(List<RequestHandler> handlers) {
        this.handlers = handlers;
    }

    public HttpResponse handle(HttpRequest request) {
        for (RequestHandler handler : handlers) {
            if (handler.canHandle(request)) {
                return handler.handle(request);
            }
        }

        return ResponseEntity.notFound("");
    }
}
