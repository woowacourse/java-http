package nextstep.jwp.http.request;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = new ConcurrentHashMap<>();
    private static final Controller STATIC_RESOURCE_CONTROLLER = new StaticResourceController();

    static {
        CONTROLLERS.put("/register", new RegisterController());
        CONTROLLERS.put("/login", new LoginController());
    }

    public Controller getController(String uri) {
        return CONTROLLERS.getOrDefault(uri, STATIC_RESOURCE_CONTROLLER);
    }
}

