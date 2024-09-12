package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.WelcomeController;

public enum ControllerMapper {

    WELCOME("/", new WelcomeController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController()),
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
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 경로에 매핑되는 컨트롤러가 없습니다."))
                .controller;
    }
}
