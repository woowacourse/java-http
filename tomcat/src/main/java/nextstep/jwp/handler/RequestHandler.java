package nextstep.jwp.handler;

import static nextstep.jwp.presentation.DefaultController.DEFAULT_PATH;
import static nextstep.jwp.presentation.LoginController.LOGIN_PATH;
import static nextstep.jwp.presentation.RegisterController.REGISTER_PATH;

import java.util.Map;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.DefaultController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.ResourceController;

public class RequestHandler {

    private static final Map<String, Controller> controllers;

    static {
        controllers = Map.of(
            DEFAULT_PATH, DefaultController.getInstance(),
            LOGIN_PATH, LoginController.getInstance(),
            REGISTER_PATH, RegisterController.getInstance()
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
