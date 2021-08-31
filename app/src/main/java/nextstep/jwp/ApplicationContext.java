package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.application.LoginService;
import nextstep.jwp.application.RegisterService;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.DefaultController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;

public class ApplicationContext {

    private static final Map<String, Controller> controllers = initController();

    private ApplicationContext() {
    }

    private static Map<String, Controller> initController() {
        Map<String, Controller> controllerMap = new HashMap<>();
        controllerMap.put("/", new DefaultController());
        controllerMap.put("/login", new LoginController(new LoginService()));
        controllerMap.put("/register", new RegisterController(new RegisterService()));

        return controllerMap;
    }

    public static Map<String, Controller> getControllers() {
        return controllers;
    }
}
