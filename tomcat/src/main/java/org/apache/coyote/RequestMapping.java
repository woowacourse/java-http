package org.apache.coyote;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.catalina.controller.HttpController;
import org.apache.catalina.controller.ResourceController;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;

public class RequestMapping {

    private final List<HttpController> controllers;

    public RequestMapping() {
        this.controllers = List.of(
                new RootController(),
                new LoginController(),
                new RegisterController(),
                new ResourceController()
        );
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
