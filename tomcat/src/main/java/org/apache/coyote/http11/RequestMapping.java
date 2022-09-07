package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.controller.StaticFileController;

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
