package org.apache.catalina.handler;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resolver.ResourceHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class ControllerHandler implements RequestHandler {

    private final HandlerMapping handlerMapping;
    private final ResourceHandler resourceHandler;

    public ControllerHandler(final HandlerMapping handlerMapping, final ResourceHandler resourceHandler) {
        this.handlerMapping = handlerMapping;
        this.resourceHandler = resourceHandler;
    }

    @Override
    public boolean canHandle(final Http11Request request) {
        return !request.parseResourcePath().contains(".")
                && handlerMapping.existsController(request.parseResourcePath());
    }

    @Override
    public void handle(final Http11Request request, final Http11Response response) {
        final String resourcePath = request.parseResourcePath();
        final Controller controller = handlerMapping.getController(resourcePath);
        if (controller == null) {
            throw new PathNotFoundException(response);
        }
        String viewPath = controller.service(request, response);
        resourceHandler.resolve(viewPath, response);
    }
}
