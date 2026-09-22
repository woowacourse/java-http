package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final Map<String, Controller> controllers = Map.of(
                "/", new HomeController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );

        final Tomcat tomcat = new Tomcat(new ControllerMapping(controllers));
        tomcat.start();
    }
}
