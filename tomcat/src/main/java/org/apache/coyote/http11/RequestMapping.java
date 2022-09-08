package org.apache.coyote.http11;

import java.util.Arrays;

import nextstep.jwp.ui.IndexController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import nextstep.jwp.ui.ResourceController;

public enum RequestMapping {

    INDEX("/index.html", IndexController.getController()),
    LOGIN("/login.html", LoginController.getController()),
    REGISTER("/register.html", RegisterController.getController()),
    RESOURCE("", ResourceController.getController());

    private final String path;
    private final Controller controller;

    RequestMapping(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller getController(String requestPath) {
        return Arrays.stream(values())
            .filter(it -> requestPath.equals(it.path))
            .findFirst()
            .orElse(RESOURCE)
            .controller;
    }
}
