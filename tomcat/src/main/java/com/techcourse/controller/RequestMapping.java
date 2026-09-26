package com.techcourse.controller;

import com.techcourse.service.LoginService;
import org.apache.catalina.controller.Controller;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(new LoginService())
    );

    public Controller getController(String path) {
        return controllers.get(path);
    }
}
