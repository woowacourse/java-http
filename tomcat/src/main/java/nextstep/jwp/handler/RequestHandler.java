package nextstep.jwp.handler;

import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.ResourceController;
import nextstep.jwp.presentation.RootController;

public class RequestHandler {

    private static final Map<String, Controller> controllers;

    static {
        controllers = Map.of(
                "/", new RootController(),
                "/login", new LoginController()
        );
    }

    public static Controller from(String path) {
        return controllers.entrySet().stream()
                .filter(entry -> entry.getKey().equals(path))
                .map(Entry::getValue)
                .findFirst()
                .orElse(new ResourceController());
    }
}
