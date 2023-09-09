package org.apache.catalina.controller;

import java.util.Map;
import org.apache.coyote.http11.Mapper;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapper implements Mapper {

    private final Map<String, AbstractController> controllers;
    private final AbstractController defaultController;

    public RequestMapper(final Map<String, AbstractController> controllers, final AbstractController defaultController) {
        this.controllers = controllers;
        this.defaultController = defaultController;
    }

    public Controller getController(final HttpRequest request) {
        String path = request.getPath();
        return controllers.getOrDefault(path, defaultController);
    }
}
