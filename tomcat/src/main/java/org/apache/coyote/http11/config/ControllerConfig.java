package org.apache.coyote.http11.config;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.service.LoginService;
import com.techcourse.service.RegisterService;
import java.util.List;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.SessionManager;

public class ControllerConfig {

    private static final List<Controller> controllers = List.of(
            new HomeController(),
            new LoginController(new LoginService(), new SessionManager()),
            new RegisterController(new RegisterService())
    );

    public static List<Controller> getControllers() {
        return controllers;
    }
}
