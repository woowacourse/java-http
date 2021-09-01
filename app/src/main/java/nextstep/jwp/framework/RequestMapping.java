package nextstep.jwp.framework;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.framework.http.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final DefaultController defaultController;

    private RequestMapping(final Map<String, Controller> controllers, final DefaultController defaultController) {
        this.controllers = controllers;
        this.defaultController = defaultController;
    }

    public static RequestMapping create() {
        return new RequestMapping(createController(), new DefaultController());
    }

    private static Map<String, Controller> createController() {
        final Map<String, Controller> controllers = new HashMap<>();

        controllers.put("login.html", new LoginController());
        controllers.put("register.html", new RegisterController());

        return controllers;
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.pathValue(), defaultController);
    }
}
