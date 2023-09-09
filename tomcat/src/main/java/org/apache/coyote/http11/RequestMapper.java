package org.apache.coyote.http11;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.DefaultController;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;

public class RequestMapper {

    private static final List<Controller> CONTROLLERS;

    static {
        CONTROLLERS = List.of(
                new LoginController(),
                new RegisterController()
        );
    }

    public Controller getController(final HttpRequest request) {
        return CONTROLLERS.stream()
                .filter(controller -> controller.isMappedController(request))
                .findAny()
                .orElse(DefaultController.getInstance());
    }
}
