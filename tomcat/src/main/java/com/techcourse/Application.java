package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.view.ResourceRenderer;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        ResourceRenderer resourceRenderer = new ResourceRenderer();
        RequestMapping requestMapping = new RequestMapping(new StaticResourceController(resourceRenderer));
        requestMapping.register("/login", new LoginController(resourceRenderer));
        requestMapping.register("/register", new RegisterController(resourceRenderer));

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
