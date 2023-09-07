package org.apache.coyote.http11;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.IndexController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.controller.UnAuthorizedController;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;

public class RequestMapping {
    private final List<Controller> controllers;

    public RequestMapping() {
        this.controllers = List.of(
                new IndexController(),
                new LoginController(),
                new UnAuthorizedController(),
                new RegisterController(),
                new RootController()
        );
    }

    public Controller getController(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(httpRequest))
                .findFirst()
                .orElseGet(DefaultController::new);
    }
}
