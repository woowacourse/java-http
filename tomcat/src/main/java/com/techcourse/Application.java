package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var requestMapping = new RequestMapping(Map.of(
                "/", new HomeController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        ));
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
