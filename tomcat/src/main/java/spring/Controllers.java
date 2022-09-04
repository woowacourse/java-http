package spring;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.view.LoginController;
import nextstep.jwp.view.WelcomeController;

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
