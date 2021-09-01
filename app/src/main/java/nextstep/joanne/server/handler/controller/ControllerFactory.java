package nextstep.joanne.server.handler.controller;

import nextstep.joanne.dashboard.controller.LoginController;
import nextstep.joanne.dashboard.controller.RegisterController;
import nextstep.joanne.dashboard.service.RegisterService;
import nextstep.joanne.dashboard.service.UserService;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {
    public static Map<String, Controller> addControllers() {
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/login", new LoginController(new UserService()));
        controllers.put("/register", new RegisterController(new RegisterService()));
        return controllers;
    }
}
