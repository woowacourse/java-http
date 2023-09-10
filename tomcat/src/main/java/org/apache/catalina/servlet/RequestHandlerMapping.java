package org.apache.catalina.servlet;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import org.apache.catalina.servlet.handler.RequestHandler;
import org.apache.coyote.http11.request.Request;

public class RequestHandlerMapping {

    private final Map<String, RequestHandler> handlers;
    private final RequestHandler defaultHandler = new StaticResourceRequestHandler();

    public RequestHandlerMapping(final Set<RequestHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        RequestHandler::getMappingPath,
                        handler -> handler
                ));
    }

    public RequestHandler getHandler(final Request request) {
        return handlers.getOrDefault(request.getPath(), defaultHandler);
    }

}
