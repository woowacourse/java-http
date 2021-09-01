package nextstep.joanne.handler.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {
    public static Map<String, Controller> addControllers() {
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        return controllers;
    }
}
