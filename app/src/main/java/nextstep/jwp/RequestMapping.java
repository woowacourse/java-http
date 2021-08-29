package nextstep.jwp;

import nextstep.jwp.controller.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new MainController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller getController(String requestUrl) {
        return controllers.getOrDefault(requestUrl, new DefaultController());
    }
}
