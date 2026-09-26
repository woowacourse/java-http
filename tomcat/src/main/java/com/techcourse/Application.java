package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var sessionManager = SessionManager.getInstance();
        final var requestMapping = new RequestMapping();
        requestMapping.addController("/", new HomeController());
        requestMapping.addController("/register", new RegisterController());
        requestMapping.addController("/login", new LoginController(sessionManager));

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
