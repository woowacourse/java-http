package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new MainController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public static Controller findController(String name) {
        return controllers.get(name);
    }
}
