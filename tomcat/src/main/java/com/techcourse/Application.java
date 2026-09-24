package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.RequestMapping;

public class Application {

    public static void main(String[] args) {
        final RequestMapping requestMapping = new RequestMapping();

        requestMapping.addController(
                "/login",
                new LoginController()
        );

        requestMapping.addController(
                "/register",
                new RegisterController()
        );

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
