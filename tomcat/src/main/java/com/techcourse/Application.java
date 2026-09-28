package com.techcourse;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;

import java.util.Map;

public class Application {

    public static void main(String[] args) {
        Map<String, Controller> controllers = Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
        final var tomcat = new Tomcat(controllers);
        tomcat.start();
    }
}
