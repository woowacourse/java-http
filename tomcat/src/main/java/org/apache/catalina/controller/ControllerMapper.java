package org.apache.catalina.controller;

import java.util.Arrays;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.WelcomeController;

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
