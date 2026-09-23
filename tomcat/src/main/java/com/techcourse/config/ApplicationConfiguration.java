package com.techcourse.config;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.controller.StaticResourceController;
import org.apache.coyote.http11.session.HttpSessionService;

import org.apache.coyote.resource.ResourceReader;

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

    private final ResourceReader resourceReader = new ResourceReader();

    private final Controller staticResourceController =
            new StaticResourceController(resourceReader);
    private final Connector connector = new Connector(requestMapping, staticResourceController, sessionService);

    private final Tomcat tomcat = new Tomcat(connector);

    public Tomcat tomcat() {
        return tomcat;
    }
}