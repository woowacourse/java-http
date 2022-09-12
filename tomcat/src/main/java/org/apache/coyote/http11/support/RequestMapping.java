package org.apache.coyote.http11.support;

import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomePageController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.UserService;

public class RequestMapping {
    private static UserService userService = new UserService();
    private static LoginService loginService = new LoginService();

    private static HomePageController homePageController = new HomePageController();
    private static RegisterController registerController = new RegisterController(userService);
    private static LoginController loginController = new LoginController(loginService);

    private final Map<String, Controller> controllers;

    private RequestMapping() {
        this.controllers = Map.of(
                "/", homePageController,
                "/register", registerController,
                "login", loginController
        );
    }

    public static Controller find(String requestUri) {
        final RequestMapping controllerSupporter = new RequestMapping();
        return controllerSupporter.findController(requestUri);
    }

    private Controller findController(String requestUri) {
        return controllers.get(requestUri);
    }
}
