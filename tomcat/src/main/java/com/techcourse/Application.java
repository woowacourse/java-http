package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.StaticResourceController;

public class Application {

    public static void main(String[] args) {
        final StaticResourceController staticResources =
                new StaticResourceController();

        final RequestMapping requestMapping =
                new RequestMapping(staticResources);

        requestMapping.register("/login", new LoginController(staticResources));
        requestMapping.register("/register", new RegisterController(staticResources));

        final Tomcat tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
