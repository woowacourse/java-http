package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.Map;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var sessionManager = new SessionManager();
        final var requestMapping = new RequestMapping(
                Map.of(
                        "/login", new LoginController(sessionManager),
                        "/register", new RegisterController()
                ),
                new StaticResourceController()
        );
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
