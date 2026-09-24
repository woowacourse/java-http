package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = initRequestMapping();

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }

    public static RequestMapping initRequestMapping() {
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.register("/", new RootController());
        requestMapping.register("/login", new LoginController());
        requestMapping.register("/register", new RegisterController());
        return requestMapping;
    }
}
