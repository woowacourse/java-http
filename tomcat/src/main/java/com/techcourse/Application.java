package com.techcourse;

import com.techcourse.controller.HelloController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.FrontController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final RequestMapping requestMapping = new RequestMapping();

        requestMapping.addController("/", new HelloController());
        requestMapping.addController("/login", new LoginController());
        requestMapping.addController("/register", new RegisterController());
        final FrontController frontController = new FrontController(requestMapping);
        final var tomcat = new Tomcat();
        tomcat.start(frontController);
    }
}
