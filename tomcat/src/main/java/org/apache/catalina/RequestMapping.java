package org.apache.catalina;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;

public class RequestMapping {

    private final SessionManager sessionManager;

    public RequestMapping(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        if (path.equals("/")) {
            return new HomeController();
        }

        if (path.contains("/login") && request.getHttpMethod().equals(HttpMethod.POST)) {
            return new LoginController(sessionManager);
        }

        if (path.contains("/register") && request.getHttpMethod().equals(HttpMethod.POST)) {
            return new RegisterController(sessionManager);
        }

        return new StaticResourceController();
    }
}
