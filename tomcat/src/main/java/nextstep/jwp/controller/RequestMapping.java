package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/register", new RegisterController());
        controllers.put("/login", new LoginController());
    }

    public static Controller getController(String requestUrl) {
        return controllers.get(requestUrl);
    }
}
