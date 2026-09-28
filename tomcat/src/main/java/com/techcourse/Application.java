package com.techcourse;

import java.util.Map;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.controller.StaticResourceController;

public class Application {

    public static void main(String[] args) {
        Map<String, Controller> controllers = Map.of(
                "/", new RootController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );

        final var tomcat = new Tomcat(
                controllers,
                new StaticResourceController()
        );
        tomcat.start();
    }
}
