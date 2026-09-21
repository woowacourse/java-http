package com.techcourse;

import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.LogoutController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.Dispatcher;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Adapter;

public class Application {

    public static void main(String[] args) {
        final Controller indexController = new IndexController();
        final RequestMapping requestMapping = new RequestMapping(Map.of(
                "/", indexController,
                "/index.html", indexController,
                "/login", new LoginController(),
                "/logout", new LogoutController(),
                "/register", new RegisterController()
        ));

        final Adapter adapter = new Dispatcher(requestMapping, new StaticResourceController());

        final var tomcat = new Tomcat(adapter);
        tomcat.start();
    }
}
