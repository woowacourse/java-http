package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class RequestHandlerMapper {

    private final List<RequestHandler> handlers = new ArrayList<>();

    public void register(RequestHandler handler) {
        handlers.add(handler);
    }

    public HttpResponse handle(HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No handler found for request")) // TODO: Handle as 404
                .handle(request);
    }
}
