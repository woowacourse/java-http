package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.http11.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();

        final RequestMapping requestMapping = RequestMapping.getInstance();
        requestMapping.addApplicationController(new HomeController())
                .addApplicationController(new LoginController())
                .addApplicationController(new RegisterController());

        tomcat.start();
    }
}
