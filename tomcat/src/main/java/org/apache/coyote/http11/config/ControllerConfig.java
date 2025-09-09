package org.apache.coyote.http11.config;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.service.LoginService;
import java.util.List;
import org.apache.coyote.http11.controller.Controller;

public class ControllerConfig {

    private static final List<Controller> controllers = List.of(
            new HomeController(),
            new LoginController(new LoginService())
    );

    public static List<Controller> getControllers() {
        return controllers;
    }
}
