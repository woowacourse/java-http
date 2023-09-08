package org.apache.coyote;

import org.apache.catalina.controller.HttpController;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;

public class RequestMapping {

    private final List<HttpController> controllers;

    public RequestMapping(final List<HttpController> controllers) {
        this.controllers = controllers;
    }

    public HttpController getController(final HttpRequest httpRequest) {
        for (final HttpController controller : controllers) {
            if (controller.canHandle(httpRequest)) {
                return controller;
            }
        }
        return null;
    }
}
