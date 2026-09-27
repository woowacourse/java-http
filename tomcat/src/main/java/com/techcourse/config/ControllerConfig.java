package com.techcourse.config;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.StaticResourceController;

public class ControllerConfig {

    public static RequestMapping forSession(Session session) {
        return new RequestMapping(
                Map.of("/", new RootController(),
                        "/login", new LoginController(session),
                        "/register", new RegisterController()),
                new StaticResourceController());
    }
}
