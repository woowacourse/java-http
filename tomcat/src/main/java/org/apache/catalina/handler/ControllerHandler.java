package org.apache.catalina.handler;

import org.apache.catalina.Controller;
import org.apache.catalina.RequestHandler;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.Resolver;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class ControllerHandler implements RequestHandler {

    private final RequestMapping requestMapper;
    private final Resolver viewResolver;

    public ControllerHandler(final RequestMapping requestMapper, final Resolver viewResolver) {
        this.requestMapper = requestMapper;
        this.viewResolver = viewResolver;
    }

    @Override
    public boolean canHandle(final Http11Request request) {
        return !ExtensionHandler.hasFileExtension(request.parseResourcePath())
                && requestMapper.isExistsController(request.parseResourcePath());
    }

    @Override
    public void handle(final Http11Request request, final Http11Response response) {
        final String resourcePath = request.parseResourcePath();
        final Controller controller = requestMapper.getController(resourcePath);
        if (controller == null) {
            throw new PathNotFoundException(response);
        }
        String viewPath = controller.handle(request, response);
        viewResolver.resolve(viewPath, response);
    }
}
