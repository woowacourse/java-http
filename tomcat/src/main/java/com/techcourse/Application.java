package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(createRequestMapping());
        tomcat.start();
    }

    private static RequestMapping createRequestMapping() {
        RequestMapping requestMapping = new RequestMapping();
        LoginController loginController = new LoginController();
        RegisterController registerController = new RegisterController();
        requestMapping.register("/", new HomeController());
        requestMapping.register("/login", loginController);
        requestMapping.register("/login.html", loginController);
        requestMapping.register("/register", registerController);
        requestMapping.register("/register.html", registerController);

        return requestMapping;
    }
}
