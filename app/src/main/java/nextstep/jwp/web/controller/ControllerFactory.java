package nextstep.jwp.web.controller;

import nextstep.jwp.dashboard.ui.LoginController;
import nextstep.jwp.dashboard.ui.RegisterController;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {

    private ControllerFactory() {}

    public static Map<String, Controller> create() {
        final Map<String, Controller> controllers = new HashMap<>();

        final Controller loginController = new LoginController("/login");
        final Controller registerController = new RegisterController("/register");

        controllers.put(loginController.getResource(), loginController);
        controllers.put(registerController.getResource(), registerController);
        return controllers;
    }
}
