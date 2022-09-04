package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.DefaultController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.ResourceController;

public class RequestHandler {

    private static final Map<String, Controller> controllers;

    static {
        controllers = Map.of(
            "/", DefaultController.getInstance(),
            "/login", LoginController.getInstance()
        );
    }

    public static Controller findController(String path) {
        return controllers.entrySet().stream()
            .filter(entry -> entry.getKey().equals(path))
            .map(entry -> entry.getValue())
            .findAny()
            .orElse(ResourceController.getInstance());
    }
}
