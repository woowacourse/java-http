package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;

import java.util.Map;

public class Application {

    public static void main(String[] args) {
        final Map<String, Controller> controllers = Map.of(
                "/", new RootController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
        final var requestMapping = new RequestMapping(controllers, new StaticResourceController());
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
