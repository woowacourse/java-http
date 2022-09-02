package nextstep.jwp.ui.controller;

import java.util.List;
import java.util.NoSuchElementException;

public class Controllers {

    private final List<Controller> controllers;

    public Controllers() {
        this.controllers = List.of(
                new WelcomeController(),
                new LoginController()
        );
    }

    public Controller findController(final String requestUri) {
        return controllers.stream()
                .filter(controller -> controller.support(requestUri))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }
}
