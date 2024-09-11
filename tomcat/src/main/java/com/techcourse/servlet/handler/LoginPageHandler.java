package com.techcourse.servlet.handler;

import com.techcourse.servlet.Handler;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(LoginPageHandler.class);

    private static final HttpMethod METHOD = HttpMethod.GET;
    private static final String PATH = "/login";

    private final SessionManager sessionManager;

    public LoginPageHandler() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public boolean support(HttpRequest request) {
        return request.getPath().equals(PATH)
                && request.getMethod() == METHOD
                && request.getQueryParameters().isEmpty();
    }

    @Override
    public View handle(HttpRequest request) {
        Session session = sessionManager.findSession(request.getCookie().getSessionId());
        if (session == null) {
            return View.htmlBuilder()
                    .staticResource("/login.html")
                    .build();
        }
        return View.redirect("/index.html");
    }
}
