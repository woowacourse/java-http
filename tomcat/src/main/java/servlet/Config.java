package servlet;

import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.controller.WelcomeController;
import nextstep.jwp.controller.exception.ExceptionHandler;
import nextstep.jwp.controller.exception.NotFoundHandler;
import nextstep.jwp.controller.exception.UnauthorizedHandler;
import nextstep.jwp.service.UserService;

public class Config {

    private static final Config CONFIG = new Config();

    private final UserService userService = new UserService();

    private final List<Controller> controllers =
            List.of(new ResourceController(), new WelcomeController(),
            new LoginController(userService), new RegisterController(userService));
    private final List<ExceptionHandler> exceptionHandlers =
            List.of(new NotFoundHandler(), new UnauthorizedHandler());

    private Config() {}

    public static Config get() {
        return CONFIG;
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public List<ExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }
}
