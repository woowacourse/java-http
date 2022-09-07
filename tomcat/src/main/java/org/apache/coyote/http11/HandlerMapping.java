package org.apache.coyote.http11;

import java.util.Arrays;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.StaticController;
import org.apache.coyote.http11.request.HttpRequest;

public enum HandlerMapping {
    LOGIN("/login", new LoginController()),
    STATIC("static", new StaticController());

    private final String path;
    private final Controller controller;

    HandlerMapping(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static HandlerAdapter getMethodHandler(HttpRequest request) {
        String path = request.path();
        Controller controller = Arrays.stream(values())
                .filter(requestMapping -> requestMapping.path.equals(path))
                .findAny()
                .orElse(STATIC)
                .controller;
        return HandlerAdapter.from(controller, request);
    }
}
