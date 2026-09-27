package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.controller.StaticResourceController;

public class Application {

    public static void main(String[] args) {
        StaticResourceController staticResources = new StaticResourceController("static");

        RequestMapping mapping = new RequestMapping(staticResources);
        mapping.register("/", new HomeController());
        mapping.register("/login", new LoginController(staticResources));
        mapping.register("/register", new RegisterController(staticResources));

        final var tomcat = new Tomcat(mapping);
        tomcat.start();
    }
}
