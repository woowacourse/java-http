package org.apache.coyote.servlet.servlets;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

public class LoginServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    public LoginServlet(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (sessionManager.isLoginAccount(httpRequest)) {
            httpResponse.setStatusCode(StatusCode.FOUND)
                .setLocation("/index.html");
            return;
        }
        httpResponse.setStatusCode(StatusCode.OK)
            .setBody("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> bodies = httpRequest.getBodies();
        final Optional<User> user = UserService.find(bodies.get("account"), bodies.get("password"));
        if (user.isEmpty()) {
            setUnauthorized(httpResponse);
            return;
        }

        log.info("login success to ID : {}", user.get().getAccount());
        httpResponse.setStatusCode(StatusCode.FOUND)
            .setLocation("/index.html")
            .generateSessionId();
        sessionManager.add(user.get(), httpResponse);
    }
}
