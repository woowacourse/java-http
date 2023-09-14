package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.util.Set;

public class RequestMapping {

    private final Set<Controller> controllers;

    public RequestMapping(Set<Controller> controllers) {
        this.controllers = controllers;
    }

    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        getControllerFor(httpRequest).handle(httpRequest, httpResponse);
    }

    private Controller getControllerFor(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(it -> it.supports(httpRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("적절한 컨트롤러를 찾지 못했습니다"));
    }
}
