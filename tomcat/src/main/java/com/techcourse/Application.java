package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.controller.ControllerExecutor;
import org.apache.coyote.http11.session.SessionManager;

import java.util.Map;

public class Application {

    public static void main(final String[] args) {

        final ControllerExecutor controllerExecutor = new ControllerExecutor(
                Map.of("/login", new LoginController(),
                        "/register", new RegisterController())
        );
        final SessionManager sessionManager = new SessionManager();
        final Connector connector = new Connector(controllerExecutor, sessionManager);

        final var tomcat = new Tomcat(connector);
        tomcat.start();
    }
}
