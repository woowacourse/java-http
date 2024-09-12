package org.apache.coyote.controller;

import com.techcourse.controller.BasicController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerMapper {

    private ControllerMapper() {

    }

    public static Map<String, Controller> getControllers() {
        Map<String, Controller> controllers = new ConcurrentHashMap<>();
        controllers.put("/", new BasicController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        return controllers;
    }
}
