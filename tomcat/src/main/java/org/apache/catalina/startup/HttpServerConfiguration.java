package org.apache.catalina.startup;

import java.util.Map;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequestDispatcher;
import org.apache.coyote.http11.LoginController;
import org.apache.coyote.http11.RegisterController;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.SessionResolver;
import org.apache.coyote.http11.StaticResourceController;

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
