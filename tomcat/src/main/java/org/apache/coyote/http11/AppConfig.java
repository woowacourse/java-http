package org.apache.coyote.http11;

import java.util.List;

import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.StaticResourceController;

public class AppConfig {

    public static RequestMapping initRequestMapping() {
        final StaticResourceController staticResourceController = new StaticResourceController();
        final List<Controller> dynamicControllers = List.of(
                new HomeController(),
                new LoginController(),
                new RegisterController()
        );
        return new RequestMapping(staticResourceController, dynamicControllers);
    }
}
