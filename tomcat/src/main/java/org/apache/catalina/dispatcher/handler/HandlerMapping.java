package org.apache.catalina.dispatcher.handler;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;
import java.util.Optional;

public class HandlerMapping {

    private final List<Handler> handlers;

    public HandlerMapping(List<Handler> handlers) {
        this.handlers = List.copyOf(handlers);
    }

    public Optional<Handler> findHandler(HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.supports(request))
                .findFirst();
    }

}
