package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final RequestMapping requestMapping = new RequestMapping()
                .register("/", new HomeController())
                .register("/login", new LoginController())
                .register("/register", new RegisterController());

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
