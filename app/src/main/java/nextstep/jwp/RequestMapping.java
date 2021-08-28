package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new DefaultController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller getController(String requestUrl) {
        return controllers.get(requestUrl);
    }
}
