package org.apache.coyote.http11;

import java.util.HashSet;
import java.util.Set;

public class RequestHandlerMapper {

    private final Set<RequestHandler> handlers = new HashSet<>();

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
