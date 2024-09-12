package org.apache.coyote.http11.controller;

import java.util.Arrays;

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
