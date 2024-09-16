package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.NotFoundController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.WelcomeController;

public enum ControllerMapper {

    WELCOME("/", new WelcomeController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController()),
    NOT_FOUND("", new NotFoundController())
    ;

    private final String path;
    private final Controller controller;

    ControllerMapper(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller find(String path) {
        return Arrays.stream(values())
                .filter(value -> value.path.equals(path))
                .findAny()
                .orElse(NOT_FOUND)
                .controller;
    }
}
