package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandlerMapper {

    private static final Logger log = LoggerFactory.getLogger(RequestHandlerMapper.class);
    private static StaticResourceHandler resourceHandler = new StaticResourceHandler();
    private static PathRequestHandler pathHandler = new PathRequestHandler(resourceHandler);

    public RequestHandler getHandler(final HttpRequest httpRequest) {
        log.debug("requst = {}", httpRequest);
        if (pathHandler.handleable(httpRequest)) {
            return pathHandler;
        }
        return resourceHandler;
    }
}
