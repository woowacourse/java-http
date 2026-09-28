package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.RequestMapping;

import java.util.Map;

public class Application {

    public static void main(String[] args) {
        final Map<String, Controller> controllers = Map.of("/login", new LoginController(), "/register", new RegisterController());

        final RequestMapping requestMapping = new RequestMapping(controllers, new StaticResourceController());

        final Tomcat tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
