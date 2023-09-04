package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;

public class RequestHandlerMapper {

    private static StaticResourceHandler resourceHandler = new StaticResourceHandler();
    private static PathRequestHandler pathHandler = new PathRequestHandler(resourceHandler);

    public RequestHandler getHandler(final HttpRequest httpRequest) {
        if (pathHandler.handleable(httpRequest)) {
            return pathHandler;
        }
        return resourceHandler;
    }
}
