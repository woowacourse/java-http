package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.http.request.HttpRequest;

public class Controllers {

    private final List<Controller> controllers;

    public Controllers() {
        this.controllers = List.of(
            new DefaultController(),
            new LoginController(),
            new RegisterController()
        );
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllers.stream()
            .filter(controller -> controller.matchRequest(httpRequest))
            .findAny()
            .orElseGet(StaticResourceController::new);
    }
}
