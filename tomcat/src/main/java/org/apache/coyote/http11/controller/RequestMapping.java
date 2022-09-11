package org.apache.coyote.http11.controller;

import java.util.Arrays;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;

public enum RequestMapping {

    ROOT("/", new RootController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController());

    private final String path;
    private final Controller controller;

    RequestMapping(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller getController(HttpRequest httpRequest) {
        return Arrays.stream(values())
                .filter(request -> httpRequest.getPath().equals(request.path))
                .map(request -> request.controller)
                .findFirst()
                .orElse(new StaticFileController());
    }
}
