package com.techcourse.controller;

import com.techcourse.service.LoginService;
import com.techcourse.service.RegisterService;
import org.apache.catalina.controller.Controller;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(new LoginService()),
            "/register", new RegisterController(new RegisterService())
    );

    public Controller getController(String path) {
        return controllers.get(path);
    }
}
