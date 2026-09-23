package com.techcourse.config;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Controller;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.StaticResourceController;
import org.apache.coyote.http11.HttpSessionService;

import java.util.Map;

public class ApplicationConfiguration {

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final HttpSessionService sessionService =
            new HttpSessionService(sessionManager);

    private final Controller loginController = new LoginController(sessionService);


    private final Controller registerController = new RegisterController();

    private final RequestMapping requestMapping =
            new RequestMapping(
                    Map.of(LOGIN_PATH,
                            loginController,
                            REGISTER_PATH,
                            registerController
                    )
            );

    private final Controller staticResourceController = new StaticResourceController();

    private final Connector connector = new Connector(requestMapping, staticResourceController, sessionService);

    private final Tomcat tomcat = new Tomcat(connector);

    public Tomcat tomcat() {
        return tomcat;
    }
}