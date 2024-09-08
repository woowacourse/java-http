package com.techcourse.controller;

import java.util.Map;
import java.util.function.Predicate;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Controller defaultController;
    private final Map<Predicate<HttpRequest>, Controller> controllers;

    public RequestMapping() {
        this.defaultController = new ResourceController();
        this.controllers = Map.of(
                req -> req.path().equals("/"), new HomeController(),
                req -> req.path().equals("/login"), new LoginController(),
                req -> req.path().equals("/register"), new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        return controllers.entrySet().stream()
                .filter((entry) -> entry.getKey().test(request))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(defaultController);
    }
}
