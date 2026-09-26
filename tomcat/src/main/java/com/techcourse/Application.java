package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.mvc.controller.RequestMapping;

public class Application {

    public static void main(String[] args) {
        final var requestMapping = new RequestMapping();
        requestMapping.addController("/", new HomeController());
        requestMapping.addController("/register", new RegisterController());
        requestMapping.addController("/login", new LoginController());

        final var tomcat = new Tomcat();
        tomcat.addHandler((request, response) ->
                requestMapping.getController(request).service(request, response));
        tomcat.start();
    }
}
