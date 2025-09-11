package org.apache.catalina.handler;

import org.apache.catalina.RequestHandler;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resolver.ViewResolver;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class ControllerHandler implements RequestHandler {

    private final RequestMapping requestMapping;
    private final ViewResolver viewResolver;

    public ControllerHandler(final RequestMapping requestMapping, final ViewResolver viewResolver) {
        this.requestMapping = requestMapping;
        this.viewResolver = viewResolver;
    }

    @Override
    public boolean canHandle(final Http11Request request) {
        return !request.parseResourcePath().contains(".")
                && requestMapping.existsController(request.parseResourcePath());
    }

    @Override
    public void handle(final Http11Request request, final Http11Response response) {
        final String resourcePath = request.parseResourcePath();
        final Controller controller = requestMapping.getController(resourcePath);
        if (controller == null) {
            throw new PathNotFoundException(response);
        }
        String viewPath = controller.handle(request, response);
        viewResolver.resolve(viewPath, response);
    }
}
