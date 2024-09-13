package com.techcourse.controller;

import java.util.List;

import org.apache.coyote.http.HttpRequest;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();

    private final List<Controller> controllers;

    private RequestMapping() {
        controllers = List.of(
                new RootController(),
                new LoginController(),
                new RegisterController(),
                new StaticResourceController(),
                new NotFoundController());
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public Controller getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(request))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
