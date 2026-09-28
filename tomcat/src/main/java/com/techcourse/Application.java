package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticResourceController;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.StaticResourceHandler;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(createRequestMapping());
        tomcat.start();
    }

    public static RequestMapping createRequestMapping() {
        StaticResourceHandler resourceHandler = new StaticResourceHandler();
        Map<String, Controller> controllers = Map.of(
                "/", new RootController(),
                "/login", new LoginController(resourceHandler),
                "/register", new RegisterController(resourceHandler));

        return new RequestMapping(controllers, new StaticResourceController(resourceHandler));
    }
}
