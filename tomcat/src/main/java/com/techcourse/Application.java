package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

import java.util.Map;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(createRequestMapping());
        tomcat.start();
    }

    public static RequestMapping createRequestMapping() {
        return new RequestMapping(Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController()
        ), new StaticResourceController());
    }
}
