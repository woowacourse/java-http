package nextstep.joanne.server.handler.controller;

import nextstep.joanne.dashboard.controller.LoginController;
import nextstep.joanne.dashboard.controller.RegisterController;
import nextstep.joanne.dashboard.service.LoginService;
import nextstep.joanne.dashboard.service.RegisterService;

import java.util.Map;

public class ControllerFactory {
    private static final Map<String, Controller> CONTROLLERS;

    static {
        CONTROLLERS = Map.of(
                "/login", new LoginController(new LoginService()),
                "/register", new RegisterController(new RegisterService())
        );
    }

    public static Map<String, Controller> addControllers() {
        return CONTROLLERS;
    }
}
