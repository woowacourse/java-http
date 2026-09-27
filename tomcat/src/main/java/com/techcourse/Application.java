package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.resource.ResourceHandler;
import org.apache.catalina.startup.Tomcat;

import java.util.Map;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(createRequestMapping());
        tomcat.start();
    }

    public static RequestMapping createRequestMapping() {
        final var resources = new ResourceHandler();
        return new RequestMapping(Map.of(
                "/", new HomeController(),
                "/login", new LoginController(resources),
                "/register", new RegisterController()
        ), new StaticResourceController(resources));
    }
}
