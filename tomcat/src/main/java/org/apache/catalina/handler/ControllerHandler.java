package org.apache.catalina.handler;

import org.apache.catalina.Controller;
import org.apache.catalina.resolver.ViewResolver;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class ControllerHandler implements RequestHandler {

    private final HandlerMapping handlerMapping;
    private final ViewResolver viewResolver;

    public ControllerHandler(HandlerMapping handlerMapping, ViewResolver viewResolver) {
        this.handlerMapping = handlerMapping;
        this.viewResolver = viewResolver;
    }

    @Override
    public boolean canHandle(Http11Request request) {
        return !request.parseResourcePath().contains(".")
                && handlerMapping.existsController(request.parseResourcePath());
    }

    @Override
    public void handle(Http11Request request, Http11Response response) {
        String resourcePath = request.parseResourcePath();
        Controller controller = handlerMapping.getController(resourcePath);
        controller.service(request, response);
        viewResolver.resolve(resourcePath, response);
    }
}
