package org.apache.catalina.connector;

import java.util.List;
import org.apache.catalina.handler.RequestHandler;
import org.apache.catalina.resolver.ViewResolver;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerDispatcher {

    private static final Logger log = LoggerFactory.getLogger(HandlerDispatcher.class);

    private final List<RequestHandler> requestHandlers;
    private final ViewResolver viewResolver;

    public HandlerDispatcher(List<RequestHandler> requestHandlers, ViewResolver viewResolver) {
        this.requestHandlers = requestHandlers;
        this.viewResolver = viewResolver;
    }

    public void handle(final Http11Request request, final Http11Response response) {
        try {
            RequestHandler requestHandler = getRequestHandler(request);
            requestHandler.handle(request, response);
        } catch (Exception e) {
            log.warn("Not Found Request Path: {}", request.parseResourcePath(), e);
            response.setState(404);
            viewResolver.resolve("/404", response);
        }
    }

    private RequestHandler getRequestHandler(Http11Request request) {
        return requestHandlers.stream().filter(requestHandler -> requestHandler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No handler found for the request"));
    }
}
