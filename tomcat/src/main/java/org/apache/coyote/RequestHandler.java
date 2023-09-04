package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class RequestHandler {

    private final Map<String, Controller> controllers;

    public RequestHandler(final Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    public boolean canHandle(final String target) {
        return controllers.containsKey(target);
    }

    public Controller getHandler(final HttpRequest httpRequest) {
        final String target = httpRequest.getTarget();
        if (controllers.get(target) != null) {
            return controllers.get(target);
        }
        return new ResourceController();
    }
}
