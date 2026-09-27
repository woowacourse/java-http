package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;

public class Application {

    public static void main(String[] args) {
        final RequestMapping requestMapping = new RequestMapping(new StaticController());

        final Controller loginController = new LoginController();
        requestMapping.register("/login", loginController);
        requestMapping.register("/login.html", loginController);

        final Controller registerController = new RegisterController();
        requestMapping.register("/register", registerController);
        requestMapping.register("/register.html", registerController);

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
