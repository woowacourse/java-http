package org.apache.catalina;

import java.util.List;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;

public class RequestMapping {

    private final Controller staticResourceController;
    private final Controller notFoundController;
    private final List<Controller> dynamicResourceControllers;

    public RequestMapping(
            final Controller staticResourceController,
            final Controller notFoundController,
            final List<Controller> dynamicResourceControllers
    ) {
        this.staticResourceController = staticResourceController;
        this.notFoundController = notFoundController;
        this.dynamicResourceControllers = dynamicResourceControllers;
    }

    public Controller findController(final HttpRequest request) {
        final HttpRequestLine requestLine = request.getRequestLine();
        if (requestLine.isStaticResourceUri()) {
            return staticResourceController;
        }

        return dynamicResourceControllers.stream()
                .filter(controller -> controller.canHandleRequest(request))
                .findAny()
                .orElse(notFoundController);
    }
}
