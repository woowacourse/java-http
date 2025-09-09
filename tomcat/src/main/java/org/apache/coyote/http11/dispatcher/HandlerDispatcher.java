package org.apache.coyote.http11.dispatcher;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class HandlerDispatcher {

    private final List<RequestHandler> handlers;

    public HandlerDispatcher(List<RequestHandler> handlers) {
        this.handlers = handlers;
    }

    public void handle(HttpRequest request, HttpResponse response) {
        for (RequestHandler handler : handlers) {
            if (handler.canHandle(request)) {
                handler.handle(request, response);
                return;
            }
        }

        response.setHttpResponse(ResponseEntity.notFound(""));
    }
}
