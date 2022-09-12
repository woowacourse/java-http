package org.apache.coyote.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandlerMapping {

    private static Set<AbstractController> controllers = new HashSet<>();

    public HandlerMapping(final Set<AbstractController> controllers) {
        this.controllers = controllers;
    }

    public Controller findFromUri(final String uri, final String httpMethod) {
        return controllers.stream()
                .filter(it -> it.support(uri, httpMethod))
                .findAny()
                .orElse(null);
    }
}
