package org.apache.catalina.handler;

import org.apache.catalina.Controller;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resolver.ViewResolver;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class ControllerHandler implements RequestHandler {

    private final HandlerMapping handlerMapping;
    private final ViewResolver viewResolver;

    public ControllerHandler(final HandlerMapping handlerMapping, final ViewResolver viewResolver) {
        this.handlerMapping = handlerMapping;
        this.viewResolver = viewResolver;
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
        controller.service(request, response);
        viewResolver.resolve(resourcePath, response);
    }
}
