package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.List;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = new RequestMapping(
                new StaticResourceController(),
                List.of(new RootController(), new LoginController(), new RegisterController())
        );

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
