package org.apache.coyote.http11;

import java.util.Arrays;

import org.apache.coyote.http11.model.HttpRequestURI;

import nextstep.jwp.ui.IndexController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import nextstep.jwp.ui.ResourceController;

public enum RequestMapping {

    INDEX(HttpRequestURI.from("/index"), IndexController.getController()),
    LOGIN(HttpRequestURI.from("/login"), LoginController.getController()),
    REGISTER(HttpRequestURI.from("/register"), RegisterController.getController()),
    RESOURCE(HttpRequestURI.from(""), ResourceController.getController());

    private final String path;
    private final Controller controller;

    RequestMapping(HttpRequestURI requestURI, Controller controller) {
        this.path = requestURI.getPath();
        this.controller = controller;
    }

    public static Controller getController(HttpRequestURI requestURI) {
        String requestPath = requestURI.getPath();
        return Arrays.stream(values())
            .filter(it -> requestPath.equals(it.path))
            .findFirst()
            .orElse(RESOURCE)
            .controller;
    }
}
