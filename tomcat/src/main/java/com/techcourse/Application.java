package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.resource.StaticResourceController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var staticResourceController = new StaticResourceController();
        final var requestMapping = new RequestMapping(
                Map.of(
                        "/", new HomeController(),
                        "/login", new LoginController(staticResourceController),
                        "/register", new RegisterController(staticResourceController)
                ),
                staticResourceController
        );
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
