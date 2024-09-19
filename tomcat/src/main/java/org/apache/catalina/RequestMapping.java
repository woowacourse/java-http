package org.apache.catalina;

import java.util.List;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;

public class RequestMapping {

    private final List<Controller> dynamicResourceControllers;
    private final Controller staticResourceController;

    public RequestMapping(
            final Controller staticResourceController,
            final List<Controller> dynamicResourceControllers
    ) {
        this.dynamicResourceControllers = dynamicResourceControllers;
        this.staticResourceController = staticResourceController;
    }

    public Controller findController(final HttpRequest request) {
        return dynamicResourceControllers.stream()
                .filter(controller -> controller.canHandle(request))
                .findAny()
                .orElse(staticResourceController);
    }
}
