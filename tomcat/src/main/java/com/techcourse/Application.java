package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.StaticResourceController;
import org.apache.coyote.http11.StaticResourceRenderer;

public class Application {

    public static void main(String[] args) {
        StaticResourceRenderer resourceRenderer = new StaticResourceRenderer();
        RequestMapping requestMapping = new RequestMapping(
                new StaticResourceController(resourceRenderer)
        );
        requestMapping.register("/", new RootController());
        requestMapping.register("/register", new RegisterController(resourceRenderer));
        requestMapping.register("/login", new LoginController(resourceRenderer));

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
