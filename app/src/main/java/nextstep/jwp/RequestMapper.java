package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class RequestMapper {
    private RequestMapper() {
    }

    private static final Map<String, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put("/login", new LoginController());
        CONTROLLERS.put("/register", new RegisterController());
    }

    public static Controller getController(String requestUri) {
        if (CONTROLLERS.containsKey(requestUri)) {
            return CONTROLLERS.get(requestUri);
        }
        return new ResourceController();
    }
}
