package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new IndexController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller getController(HttpRequest request) {
        if (CONTROLLERS.containsKey(request.getPath())) {
            return CONTROLLERS.get(request.getPath());
        }

        return new StaticFileController();
    }
}
