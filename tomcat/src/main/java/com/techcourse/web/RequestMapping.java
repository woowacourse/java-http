package com.techcourse.web;

import com.techcourse.controller.Controller;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.service.ApplicationService;
import java.util.Map;
import org.apache.coyote.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController;

    public RequestMapping(ApplicationService applicationService, StaticResourceHandler staticResourceHandler) {
        controllers = Map.of(
                "/login", new LoginController(applicationService, staticResourceHandler),
                "/register", new RegisterController(applicationService, staticResourceHandler),
                "/", new HomeController()
        );
        staticResourceController = new StaticResourceController(staticResourceHandler);
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.path(), staticResourceController);
    }

}
