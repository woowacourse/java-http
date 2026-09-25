package com.techcourse;

import java.util.Map;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.controller.StaticResourceController;

public class Application {

    public static void main(String[] args) {
        Map<String, Controller> controllersByPath = Map.of("/login", new LoginController(),
                "/register", new RegisterController(),
                "/", new StaticResourceController());
        final var requestMapping = new RequestMapping(Map.copyOf(controllersByPath));
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
