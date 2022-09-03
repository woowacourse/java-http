package org.apache.coyote.http11;

import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.controller.WelcomeController;

public class RequestMapping {

    private static final Map<String, Controller> requestMapping;

    private static final String WELCOME_PAGE_PATH = "/";
    private static final String LOGIN_PAGE_PATH = "/login";

    static {
        requestMapping = Map.of(
                WELCOME_PAGE_PATH, WelcomeController.getInstance(),
                LOGIN_PAGE_PATH, LoginController.getInstance()
        );
    }

    private RequestMapping() {
    }

    public static Controller findController(final String path) {
        return requestMapping.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(path))
                .map(Entry::getValue)
                .findFirst()
                .orElse(StaticResourceController.getInstance());
    }
}
