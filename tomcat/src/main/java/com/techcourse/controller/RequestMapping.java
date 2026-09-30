package com.techcourse.controller;

import com.techcourse.service.LoginService;
import com.techcourse.service.RegisterService;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapping;

import java.util.Map;

public class RequestMapping implements ControllerMapping {

    private final StaticResourceController staticResources = new StaticResourceController();
    private final Map<String, Controller> controllers = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(new LoginService(), staticResources),
            "/register", new RegisterController(new RegisterService(), staticResources)
    );

    @Override
    public Controller getController(String path) {
        return controllers.getOrDefault(path, staticResources);
    }
}
