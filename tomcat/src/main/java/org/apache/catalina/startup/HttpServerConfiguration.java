package org.apache.catalina.startup;

import java.util.Map;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.routing.HttpRequestDispatcher;
import org.apache.catalina.routing.RequestMapping;
import org.apache.catalina.session.SessionResolver;

public class HttpServerConfiguration {

    public Connector createConnector() {
        return new Connector(createRequestDispatcher());
    }

    private HttpRequestDispatcher createRequestDispatcher() {
        SessionManager sessionManager = new SessionManager();
        StaticResourceController staticController = new StaticResourceController();
        RequestMapping requestMapping =
                createRequestMapping(sessionManager, staticController);

        return new HttpRequestDispatcher(requestMapping, staticController);
    }

    private RequestMapping createRequestMapping(SessionManager sessionManager, StaticResourceController staticController) {
        SessionResolver sessionResolver = new SessionResolver(sessionManager);

        return new RequestMapping(Map.of(
                "/register", new RegisterController(staticController),
                "/login", new LoginController(sessionResolver, sessionManager, staticController)
        ));
    }
}
