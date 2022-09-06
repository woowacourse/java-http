package org.apache.coyote.http11;

import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.StaticResourceController;
import nextstep.jwp.presentation.WelcomeController;
import org.apache.coyote.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> requestMapping;

    private static final String WELCOME_PAGE_PATH = "/";
    private static final String LOGIN_PAGE_PATH = "/login";
    private static final String REGISTER_PAGE_PATH = "/register";

    static {
        requestMapping = Map.of(
                WELCOME_PAGE_PATH, WelcomeController.getInstance(),
                LOGIN_PAGE_PATH, LoginController.getInstance(),
                REGISTER_PAGE_PATH, RegisterController.getInstance()
        );
    }

    private RequestMapping() {
    }

    public static Controller findController(final HttpRequest request) {
        return requestMapping.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(request.getPath()))
                .map(Entry::getValue)
                .findFirst()
                .orElse(StaticResourceController.getInstance());
    }
}
