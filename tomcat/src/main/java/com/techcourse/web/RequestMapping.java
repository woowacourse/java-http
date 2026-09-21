package com.techcourse.web;

import com.techcourse.controller.Controller;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.service.ApplicationService;
import org.apache.coyote.HttpRequest;

public class RequestMapping {

    private final Controller loginController;
    private final Controller registerController;
    private final Controller homeController;
    private final Controller staticResourceController;

    public RequestMapping(ApplicationService applicationService, StaticResourceHandler staticResourceHandler) {
        loginController = new LoginController(applicationService, staticResourceHandler);
        registerController = new RegisterController(applicationService, staticResourceHandler);
        homeController = new HomeController();
        staticResourceController = new StaticResourceController(staticResourceHandler);
    }

    public Controller getController(HttpRequest request) {
        if ("/login".equals(request.path())) {
            return loginController;
        }
        if ("/register".equals(request.path())) {
            return registerController;
        }
        if ("/".equals(request.path())) {
            return homeController;
        }
        return staticResourceController;
    }

}
