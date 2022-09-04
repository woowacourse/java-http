package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.GreetingController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.support.ResourceSuffix;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapping {

    private final Map<String, Controller> mapping = new HashMap<>();

    public ControllerMapping() {
        mapping.put("/", new GreetingController());
        mapping.put("/login", new LoginController());
    }

    public Controller getController(final String uri) {
        final Controller controller = mapping.get(uri);
        if (controller != null) {
            return controller;
        }
        if (isResourceUri(uri)) {
            return new ResourceController();
        }
        throw new CustomNotFoundException(uri + "를 처리할 컨트롤러를 찾지 못함");
    }

    private boolean isResourceUri(final String uri) {
        return ResourceSuffix.isEndWith(uri);
    }
}
