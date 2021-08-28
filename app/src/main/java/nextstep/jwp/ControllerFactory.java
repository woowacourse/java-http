package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {

    public static Map<String, Controller> create() {
        final Map<String, Controller> controllers = new HashMap<>();

        final Controller loginController = new LoginController("/login");
        final Controller registerController = new RegisterController("/register");

        controllers.put(loginController.getResource(), loginController);
        controllers.put(registerController.getResource(), registerController);
        return controllers;
    }
}
