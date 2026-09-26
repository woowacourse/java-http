package com.techcourse.web;

import com.techcourse.web.controller.LoginController;
import com.techcourse.web.controller.RegisterController;
import com.techcourse.web.controller.StaticResourceController;
import java.util.Map;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private static final Controller DEFAULT_CONTROLLER = new StaticResourceController();

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        return CONTROLLERS.getOrDefault(path, DEFAULT_CONTROLLER);
    }
}
