package nextstep.jwp;

import nextstep.jwp.controller.*;
import nextstep.jwp.service.UserService;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllerMap = new HashMap<>();

    static {
        LoginController.createInstance(UserService.getUserService());
        RegisterController.createInstance(UserService.getUserService());
        controllerMap.put("/", new IndexController());
        controllerMap.put("/login", LoginController.getInstance());
        controllerMap.put("/register", RegisterController.getInstance());
    }

    private RequestMapping() {
    }

    public static Controller getController(String uri) {
        return controllerMap.getOrDefault(uri, DefaultController.getInstance());
    }
}
