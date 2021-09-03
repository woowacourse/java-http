package nextstep.jwp;

import nextstep.jwp.controller.*;
import nextstep.jwp.service.UserService;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLER_MAP = new HashMap<>();

    static {
        LoginController.createInstance(UserService.getUserService());
        RegisterController.createInstance(UserService.getUserService());
        CONTROLLER_MAP.put("/", new IndexController());
        CONTROLLER_MAP.put("/login", LoginController.getInstance());
        CONTROLLER_MAP.put("/register", RegisterController.getInstance());
    }

    private RequestMapping() {
    }

    public static Controller getController(String uri) {
        return CONTROLLER_MAP.getOrDefault(uri, DefaultController.getInstance());
    }
}
