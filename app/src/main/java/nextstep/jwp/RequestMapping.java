package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/register", new RegisterController());
        controllers.put("/login", new LoginController());
    }

    public static Controller getController(String requestUri) {
        return controllers.get(requestUri);
    }
}
