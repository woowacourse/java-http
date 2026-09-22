package org.apache.catalina.controller;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;

public class ControllerMapper {
    private static final Map<String, Controller> controllers = Map.of(
            "/index", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public static Controller getController(String path) {
        return controllers.get(path);
    }
}
