package org.apache.coyote.http11.support;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.controller.HomePageController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.UserService;
import org.apache.catalina.Controller;

public class RequestMapping {
    private static UserService userService = new UserService();
    private static LoginService loginService = new LoginService();

    private static HomePageController homePageController = new HomePageController();
    private static RegisterController registerController = new RegisterController(userService);
    private static LoginController loginController = new LoginController(loginService);

    private final Map<String, Controller> controllers;

    private RequestMapping() {
        this.controllers = new LinkedHashMap<>() {{
            put("/index", homePageController);
            put("/register", registerController);
            put("/login", loginController);
            put("/", homePageController);
        }};
    }

    public static Controller find(String requestUri) {
        final RequestMapping requestMapping = new RequestMapping();
        return requestMapping.findController(requestUri);
    }

    private Controller findController(String requestUri) {
        final String foundUrl = controllers.keySet().stream()
                .filter(requestUri::contains)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 컨트롤러가 없습니다."));
        return controllers.get(foundUrl);
    }
}
