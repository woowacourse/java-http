package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = new RequestMapping(new StaticResourceController());
        requestMapping.register("/login", new LoginController());
        requestMapping.register("/register", new RegisterController());

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
